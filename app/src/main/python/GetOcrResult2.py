import requests
import uuid
import time
import json
import base64
import re

api_url = 'https://a16c0cc0fb1d484793bb0aedc4cef7f5.apigw.ntruss.com/custom/v1/4846/10e30515c08afbe1921d1fc33f87d44809ebbb207de907499d2db0fd469c87ba/general'
secret_key = 'blBlUFNPSmxlZ0loRlVLb1ZQTGZMUHZJUUtJcVNOWVg='

def getOCRResult(name, input_image_file, format):
    request_json = {
        'images': [
            {
                'format': format,
                'name': name
            }
        ],
        'requestId': str(uuid.uuid4()),
        'version': 'V2',
        'timestamp': int(round(time.time() * 1000))
    }

    payload = {'message': json.dumps(request_json).encode('UTF-8')}
    files = [
      ('file', open(input_image_file,'rb'))
    ]
    headers = {
      'X-OCR-SECRET': secret_key
    }

    response = requests.request("POST", api_url, headers=headers, data = payload, files = files)

    result_text = processResponse(response)

    return result_text

def processResponse(input_response):
    jsonObj = json.loads(input_response.text)
    # images 필드만 추출
    images = jsonObj.get("images")
    # images 필드 중 OCR 결과 담고 있는 fields만 추출
    fields = images[0].get("fields")
    boundaryObj=[]
    # fields에서 경계 객체 boundingPoly만 추출
    for i in fields:
        boundaryObj.append(i.get('boundingPoly'))
    boundaryList=[]
    for i in boundaryObj:
        # 경계의 좌표들만 추출
        boundaryList.append(i.get('vertices'))
    # 경계의 y좌표만 추출
    yBoundList = []
    print(yBoundList)
    for i in boundaryList:
        yBoundList.append(i[0].get('y'))
    resultText=""
    strList=[]
    # 초기 y 좌표 경계값 설정
    yBoundary = yBoundList[0]
    # 각 요소들의 인덱스
    j=0 
    # 라인 인덱스
    k=0
    strList.append([])
    for i in fields:
        if j<len(fields)-1:
            # 다음 요소도 같은 라인인 경우
            if ((yBoundList[j+1]-yBoundary)<90):
                # 100미만 차이나는 것(즉 같은 라인이라고 판단되는 것)중 가장 y좌표 작은 것을 해당 라인 기준점으로 재설정
                if yBoundList[j+1]<yBoundary:
                    yBoundary=yBoundList[j+1]
                resultText = i.get('inferText')
                strList[k].append(resultText)
            # 다음 요소가 다음 라인인 경우 라인 인덱스 1 증가
            else:
                resultText = i.get('inferText')
                strList[k].append(resultText)
                yBoundary = yBoundList[j+1]
                strList.append([])
                k+=1 
        else:
            resultText = i.get('inferText')
            strList[k].append(resultText)
        j+=1
    return strList

def extractPlace(strLists):
    place=strLists[2][1]
    return place
    

def extractDate(strLists):
    date=strLists[0][0]+" "
    date+=strLists[0][1]

    return date

def extractProduct(strLists):
    items=[]
    prices=[]

    for i in range(3, len(strLists)):
        #print("i:", i, strList[i])
        if strLists[i][0]=='면세품':
            break
        temp=""
        j=0
        for j in range(len(strLists[i])-2):
            #print("j:", j, strLists[i][j])
            temp+=strLists[i][j]+" "
        # '(' 나 '★' 로 시작하면 ')'나 ★가 다시 나오기 전까지 내용 삭제
        temp=temp.replace('^[(★][[ㄱ-ㅎㅏ-ㅣ가-힣A-Za-z0-9 ~/]*[)★]', '')
        # 양끝 공백 제거
        temp=temp.strip()
        items.append(temp)
        price=strLists[i][-1]
        price=price.replace('원', '')
        price=price.replace(',', '')
        prices.append(price)
        last = i
    return [items, prices]

def getLastProductIndex(strLists):
    for i in range(4, len(strLists)):
        if strLists[i][0]=='면세품':
            break
        last = i
    return last

def extractTotalPrice(strLists):
    last=getLastProductIndex(strLists)
    totalPrice=strLists[last+3][1]
    totalPrice=totalPrice.replace('원', '')
    totalPrice=totalPrice.replace(',', '')

    return totalPrice
    
    


