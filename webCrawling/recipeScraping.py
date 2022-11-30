from bs4 import BeautifulSoup as bs
import pandas as pd
from webdriver_manager.chrome import ChromeDriverManager
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
import time
import csv

# 만개의 레시피 사이트에서 각각의 2000개의 레시피에 대하여
# title, url, 주재료를 스크랩한다.


#크롬 옵션 지정
options =webdriver.ChromeOptions()

#헤드리스 옵션 지정
# options.add_argument('--headless')

#이상한로그(작동엔 문제없음)뜨는거 없애는 옵션 지정
options.add_experimental_option("excludeSwitches",["enable-logging"])

#크롤링 막는 것을 피하기 위해 에이전트 입력(사람처럼 보이게 하기)
UserAgent= "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
options.add_argument('user-agent=' + UserAgent)

#드라이브 설정(자동 다운로드 및 대기)
driver=webdriver.Chrome(service=Service(ChromeDriverManager().install()),options=options)
driver.implicitly_wait(10)

# 저장할 파일 정하기.
f=open(r'C:\Users\gksdy\바탕 화면\python\recipeList.csv','w',encoding='UTF-8',newline='')
csvWriter=csv.writer(f)
csvWriter.writerow(['요리명','href','재료들'])

cnt=1
sign=0
for page in range(45,51):#페이지 전환을 위한 for문
    
    driver.get(f"https://www.10000recipe.com/recipe/list.html?cat2=18&order=reco&page={page}")

    menus=driver.find_elements(By.CSS_SELECTOR,".common_sp_list_li")

    foods=[]

    for menu in menus:
        href=menu.find_element(By.CSS_SELECTOR,".common_sp_thumb>a").get_attribute('href')
        name=menu.find_element(By.CSS_SELECTOR,".common_sp_caption>div.common_sp_caption_tit.line2").text
        foods.append((name,href))


    for food in foods:#메뉴전환을 위한 for문
            
        driver.get(food[1])
        
        try:
            seq1=driver.find_element(By.CSS_SELECTOR,".ready_ingre3>ul")#주재료칸이 없거나,주재료가 아예 없으면 pass~
            seq2=seq1.find_elements(By.CSS_SELECTOR,"li")
        except:
            continue
        
        recipe=[]
        recipe.append(food[0])# 요리 제목 
        recipe.append(food[1])# 요리 href
        for item in seq2:
            try:
                ingredient=item.find_element(By.CSS_SELECTOR,"a").text#예외 발생하면 그냥 ingredient pass~
                recipe.append(ingredient)
            except:
                pass
        
        csvWriter.writerow(recipe)
        print(cnt)
        cnt=cnt+1
        

f.close()


