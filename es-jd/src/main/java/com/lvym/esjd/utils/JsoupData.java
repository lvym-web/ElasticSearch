package com.lvym.esjd.utils;

import com.lvym.esjd.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsoupData {



    public List<Content> parseJD(String keyword) throws IOException {
        String url="https://search.jd.com/Search?keyword="+keyword;
        Document parse = Jsoup.parse(new URL(url), 30000);
        Element j_goodsList = parse.getElementById("J_goodsList");
        Elements li = j_goodsList.getElementsByTag("li");

        ArrayList<Content> contents = new ArrayList<>();
        for (Element element : li) {
            String img = element.getElementsByTag("img").eq(0).attr("src");
            String price = element.getElementsByClass("p-price").eq(0).text();
            String name = element.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setName(name);
            contents.add(content);
        }
        return contents;
    }
}
