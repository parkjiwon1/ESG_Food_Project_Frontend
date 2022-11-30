import requests
from bs4 import BeautifulSoup
import urllib.request
import pandas as pd


#url로 들어가서, 음식이미지,제목,내용을 스크랩한다.

file=pd.read_csv("imageUrl.csv",header=None,names=["url","imageUrl","title","content"])
df=file.copy()

for idx,url in enumerate(file["url"]):
        
    headers = {"User-Agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36"}
    res = requests.get(url, headers=headers)
    res.raise_for_status()
    soup = BeautifulSoup(res.text, "html.parser") 
    
    tempTitle=soup.find(attrs={"class":"view2_summary st3"})
    if tempTitle==None:
        title=""
    else: title=tempTitle.findChild().text.strip().replace("\n","")
    
    tempContent=soup.find(id="recipeIntro")
    if tempContent==None:
        content=""
    else: content=tempContent.text.strip().replace("\n","")
    
    tempImageUrl=soup.find(id="main_thumbs")
    if tempImageUrl==None:
        imageUrl=""
    else:
        imageUrl=tempImageUrl['src']
    
    print(f"title={title}, content={content} , imageUrl = {imageUrl}, idx={idx}")
    
    df.loc[idx,"imageUrl"]=imageUrl
    df.loc[idx,"title"]=title
    df.loc[idx,"content"]=content
    
print(df)

df.to_csv(r"C:\Users\gksdy\바탕 화면\python\imageUrl_result.csv",encoding='utf-8')

