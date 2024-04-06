package com.example.saber.mplrss;
import android.net.Uri;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RSSDocument{
    private Document document;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Uri localFileUri;
    private NodeList channelNodesList;
    public String dataExtractionState;

    private String channelTitle;
    private String channelDescription;
    private String channelLink;
    private LinkedList<RSSitem> channelItems;
    private String downloadLink;


    public RSSDocument(String downloadFilePath,String DL){
        localFileUri = Uri.parse(downloadFilePath);
        File e = new File(localFileUri.getPath());
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db =  dbf.newDocumentBuilder();
        }catch (ParserConfigurationException el){
            el.printStackTrace();
        }
        try {
            document = db.parse(e);
        } catch (IOException s) {
            s.printStackTrace();
        }catch (SAXException s){
            s.printStackTrace();
        }

        channelNodesList = document.getElementsByTagName("channel").item(0).getChildNodes();
        channelTitle = extractChannelTitle();
        channelLink = extractChannelLink();
        channelDescription = extractChannelDesc();
        channelItems = new LinkedList<>();
        extractChannelItems();
        downloadLink = DL;
        dataExtractionState = "OK";
    }
    //récuperer le contenu textuel de l'element qui contient le nom 'tagName' dans une liste de noeuds 'list'
    //s'il n'a pas de contenu textuel, routourner Chaine vide
    private String getNodeTextValue(String tagName, NodeList list){
        int i=0;
        while (i < list.getLength()){
            if (list.item(i).getNodeName().equals(tagName)){
                return list.item(i).getTextContent();
            }
            i++;
        }
        return "";
    }
    //trouver l'indice de l'element qui contient le nom 'tagName' dans une liste de noeuds 'list'
    //s'il n'est pas trouvé, routourner -1
    private int getNodeIndex(String tagName, NodeList list){
        int i=0;
        while (i < list.getLength()){
            if (list.item(i).getNodeName().equals(tagName)){
                return i;
            }
            i++;
        }
        return -1;
    }
    //extracter title de channel
    private String extractChannelTitle(){
        return getNodeTextValue("title",channelNodesList);
    }
    //extracter description de channel
    private String extractChannelDesc(){
        return getNodeTextValue("description",channelNodesList);
    }
    //extracter link de channel
    private String extractChannelLink(){
        return getNodeTextValue("link",channelNodesList);
    }
    //extracter les items de channel
    private void extractChannelItems(){
        NodeList nl = document.getElementsByTagName("item");
        String title,link,desc;
        for (int i=0;i<nl.getLength();i++){
            title = getNodeTextValue("title",nl.item(i).getChildNodes());
            link = getNodeTextValue("link",nl.item(i).getChildNodes());
            desc = getNodeTextValue("description",nl.item(i).getChildNodes());
            channelItems.add(new RSSitem(title,link,desc));//ajouter un objet RSSitem dans la list des items
        }
    }

    public String getChannelTitle(){
        return channelTitle;
    }
    public String getChannelLink(){
        return channelLink;
    }
    public String getChannelDescription(){
        return channelDescription;
    }
    public LinkedList<RSSitem>getChannelItems(){
        return channelItems;
    }
    public int getNumberOfitems(){
        return channelItems.size();
    }
    public String getDownloadLink(){
        return downloadLink;
    }
    //classe inclue
    class RSSitem{
        private String title;//title d'un item
        private String link;//link d'un item
        private String description;//description d'un item
        RSSitem (String TITLE,String LINK, String DESC){
            title = TITLE;
            link = LINK;
            description = DESC;
        }
        public String getItemTitle(){
            return title;
        }
        public String getItemLink(){
            return link;
        }
        public String getItemDescription(){
            return description;
        }
    }
}
