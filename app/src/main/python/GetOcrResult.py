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
#     jsonStr = json.dumps(jsonObj, ensure_ascii = False,indent=3)
    # images 필드만 추출
    images = jsonObj.get("images")
    # images 필드 중 OCR 결과 담고 있는 fields만 추출
    field = images[0].get('fields')
    boundaryObj=[]
    # fields에서 경계 객체 bounding만 추출
    for i in field:
        boundaryObj.append(i.get('boundingPoly'))
    boundaryList=[]
    for i in boundaryObj:
        boundaryList.append(i.get('vertices'))
    # 경계의 y좌표만 추출
    yBoundList = []
    print(yBoundList)
    for i in boundaryList:
        yBoundList.append(i[0].get('y'))
    resultText=""
    strList=[]
    yBoundary = yBoundList[0]
    # 각 요소들의 인덱스
    j=0
    # 라인 인덱스
    k=0
    strList.append([])
    for i in field:
        if j<len(field)-1:
            if ((yBoundList[j+1]-yBoundary)<30):
                # receipt_sample3에서는 "emart"를 먼저 인식하고 그 y좌표가 기준점이 되어 "~강희석"도 같은 라인으로 인식했음
                # 이를 방지하고자 30이하 차이나는 것(즉 같은 라인이라고 판단되는 것)중 가장 y좌표 작은 것을 해당 라인 기준점으로 재설정
                if yBoundList[j+1]<yBoundary:
                    yBoundary=yBoundList[j+1]
    #             resultText += str(j) + ":"
                resultText = i.get('inferText')
                strList[k].append(resultText)
            else:
    #             resultText += str(j) + ":"
                resultText = i.get('inferText')
                strList[k].append(resultText)
                yBoundary = yBoundList[j+1]
                strList.append([])
                k+=1
    #             resultText += "[새 경계:" + str(yBoundary) + "]"
        else:
            resultText = i.get('inferText')
            strList[k].append(resultText)
        j+=1
    tempStr=""
    # 마지막 라인은 추가 안 되는데 어차피 사용 안하므로 패스..
#     for i in resultText:
#         if i=='\n':
#             strList.append(tempStr)
#             tempStr=""
#         else:
#             tempStr+=i
    return strList

def extractPlace(strLists):
    strList=[]
    for i in range(len(strLists)):
        temp=""
        for j in range(len(strLists[i])):
            temp+=strLists[i][j]+" "
        strList.append(temp)
    print(strList)
    firstLineCheck = re.compile('^[a-zA-Z ]*$')
    # 첫 번째 라인부터 ex) 이마트 죽전점 ~ 인 경우도 있지만 emart인 경우도 있음
    if firstLineCheck.match(strList[0]):
        placeElement = re.findall('[가-힣]+',strList[1])
        print("두번째줄부터 장소 추출")
    else:
        placeElement = re.findall('[가-힣]+',strList[0])

    print(placeElement)
    place=""
    for i in placeElement:
        place+=i
        place+=" "
    return place

def extractDate(strLists):
    strList=[]
    for i in range(len(strLists)):
        temp=""
        for j in range(len(strLists[i])):
            temp+=strLists[i][j]+" "
        strList.append(temp)
    print(strList)
    i=0
    for i in range(len(strList)):
        if('[구 매]' in strList[i]):
            dateIndex=i
            break
    print(strList[i])
    dateElement=re.findall('[0-9\:-]+',strList[i])

    date=dateElement[0]+" "+dateElement[1]+":00"

    return date

def extractProduct(strLists):
    strList=[]
    Items=[]
    prices=[]
    for i in range(len(strLists)):
        temp=""
        for j in range(len(strLists[i])):
            temp+=strLists[i][j]+" "
        strList.append(temp)
    print(strList)
    # 품목 라인 시작 직전 라인 인덱스

    for i in range(len(strList)):
        if '금 액' in strList[i]:
            beforeindex=i
            break

    AfterIndexCheck1 = re.compile('^[총품목수량 ].*$')
    AfterIndexCheck2 = re.compile('^[과세물품 ].*$')

    for i in range(beforeindex, len(strList)):
#         if ('총 품목 수량' in strList[i]) or ('과세 물품' in strList[i]):
        if (AfterIndexCheck1.match(strList[i]) or AfterIndexCheck2.match(strList[i])):
            afterindex=i
            break
    print("beforeindex:"+str(strList[beforeindex]))
    print("afterindex:"+str(strList[afterindex]))

    typeCheck = re.compile('^[0-9, ]*$')
    flag=0
    # "품목 , 가격, 수량, 금액" 형식인지 "품목"인지 체크
    if typeCheck.match(strList[beforeindex+2]):
        flag=1
        print("플래그:"+str(flag))

    if flag==1:
        j=0
        for i in range(beforeindex+1, afterindex):
            item=""
            price=""
            if j%2==0:
                for k in range(1,len(strLists[i])):
                    item+=strLists[i][k]
                    item+=" "
                print(item)
                Items.append(item)
            else:
                if(len(strLists[i])>4):
                    temp_price=strLists[i][-2]+strLists[i][-1]
                    temp_price=temp_price.replace(',','')
                    prices.append(temp_price)
                else:
                    temp_price=strLists[i][-1]
                    temp_price=temp_price.replace(',','')
                    prices.append(temp_price)
            j+=1
    else:
        j=0
        for i in range(beforeindex+1, afterindex):
            item=""
            for k in range(0, len(strLists[i])-2):
                item+=strLists[i][k]
                item+=" "
            print(item)
            Items.append(item)
            temp_price=strLists[i][-1]
            temp_price=temp_price.replace(',','')
            prices.append(temp_price)
    print("아이템:"+str(Items))
    print("가격:"+str(prices))
    return [Items,prices]

def extractTotalPrice(strLists):
    for i in range(len(strLists)):
        if strLists[i][0]=='결제대상금액':
            totalPrice=strLists[i][1]
            return totalPrice


