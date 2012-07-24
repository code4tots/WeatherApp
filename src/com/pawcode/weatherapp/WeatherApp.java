/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pawcode.weatherapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The WeatherApp class is for getting weather data given city and country 
 * names. It gets this information using a web service from a WSDL I've found
 * on the Internet (I Googled "weather WSDL" and it was the first entry --
 * "global weather WSDL")
 * The WeatherApp has exactly 2 static methods, one of which is private.
 * It really has nothing else -- so it would be silly to instantiate anything.
 * The private method getWeather(String cityName, String countryName) returns
 * a raw XML String. getProcessedWeatherString gets this raw XML string,
 * and processes it for a bit more user friendly display.
 * @author math4tots
 */
public class WeatherApp {
    
    public static String getProcessedWeatherString(
            String cityName,
            String countryName) {
        final StringBuilder ret = new StringBuilder();
        
        String s = getWeather(cityName,countryName);
        
        if (s.equals("Data Not Found")){
            ret.append("City [").append(cityName).append("] in country [")
                    .append(countryName).append("] counld not be found");
        }
        else {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            
            try {
                SAXParser sp = spf.newSAXParser();
                sp.parse(new InputSource(
                        new ByteArrayInputStream(s.getBytes("utf-16"))), 
                        new DefaultHandler() {
                    int counter;
                    
                    @Override
                    public void endDocument() throws SAXException {
                        counter = 0;
                        super.endDocument();
                    }

                    @Override
                    public void characters(
                            char[] chars, 
                            int start, 
                            int length) throws SAXException {
                        super.characters(chars, start, length);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < length;i++){
                            sb.append(chars[start+i]);
                        }
                        
                        
                        ret.append(sb.toString().trim());
                    }

                    @Override
                    public void endElement(
                            String string, 
                            String string1, 
                            String string2) throws SAXException {
                        super.endElement(string, string1, string2);
                        counter--;
                        if (counter > 0)
                            ret.append("\n");
                    }

                    @Override
                    public void startDocument() throws SAXException {
                        super.startDocument();
                    }

                    @Override
                    public void startElement(
                            String string, 
                            String localName, 
                            String qName, 
                            Attributes atrbts) throws SAXException {
                        super.startElement(string, localName, qName, atrbts);
                        counter++;
                        
                        /* we ignore the top */
                        if (counter == 2){
                            
                            ret.append(qName).append(":");
                            
                            for (int i = 0; i < 20-qName.length(); i++){
                                ret.append(' ');
                            }
                        }
                        /* we didn't prepare for this --- */
                        else if (counter > 2){
                            System.err.println("WARNING: There's more than "
                                    + "one layer of nesting, so the "
                                    + "data may not display properly");
                        }
                        
                    }
                    
                    
                });
            } catch (IOException ex) {
                Logger.getLogger(WeatherApp.class.getName())
                        .log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(WeatherApp.class.getName())
                        .log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(WeatherApp.class.getName())
                        .log(Level.SEVERE, null, ex);
            } 
            
        }
        
        return ret.toString();
    }
    
    
    
    
    
    
    
    private static String getWeather(
            java.lang.String cityName, 
            java.lang.String countryName) {
        
        com.pawcode.weatherapp.service.GlobalWeather service =
                new com.pawcode.weatherapp.service.GlobalWeather();
        com.pawcode.weatherapp.service.GlobalWeatherSoap port = 
                service.getGlobalWeatherSoap();
        return port.getWeather(cityName, countryName);
    }
}
