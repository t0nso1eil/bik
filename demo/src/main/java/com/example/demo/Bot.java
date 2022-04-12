package com.example.demo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

@Component
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    int[] score={0,0};
    int n;
    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage sendMessage = new SendMessage();
            Long chatId = update.getMessage().getChatId();
            String message = String.valueOf(update.getMessage().getText());
            sendMessage.setChatId(String.valueOf(chatId));
            if (message.equals("/start")) {
                n=generate();
                sendMessage.setText("Давайте сыграем в игру! Мы загадали число. Попробуйте его угадать)");
            } else{
                score[0]+=game(score[0],score[1],n,message)[0];
                score[1]+=game(score[0],score[1],n,message)[1];
                sendMessage.setText(textGenerate(score));
            }
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String getBotUsername() {
        return botUsername;
    }
    public String getBotToken() {
        return botToken;
    }

    ArrayList<String> usersNames = new ArrayList<>();
    public static int generate(){
        int[] arr = new int[4];
        arr[0] = 1 + (int) (Math.random() * 9);
        for (int i = 1; i < 4; i++) {
            arr[i] = (int) (Math.random() * 10);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                while (arr[i] == arr[j]) {
                    arr[j] = (int) (Math.random() * 10);
                }
            }
        }
        int n=arr[0]*1000+arr[1]*100+arr[2]*10+arr[3];
        return n;
    }
    public static int[] game(int cow, int ox, int n, String str){
        int[] attempt = new int[4];
        int[] check=new int[4];
        int[]arr=new int[4];
        for (int i=0;i<str.length();i++){
            arr[i]=(int) str.charAt(i);
        }
        for (int i=0;i<4;i++){
            check[i]=0;
        }
        for (int i =3;i>=0;i--){
            attempt[i]=n%10;
            n=n/10;
        }
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                if (arr[i]==attempt[j]){
                    if (i==j){
                        check[i]=1;
                        ox++;
                    } else {
                        cow++;
                    }
                }
            }
        }
         int[] score=new int[2];
        score[0]=ox;
        score[1]=cow;
        int[] temp=cheat(arr,check);
        for (int a=0;a<4;a++){
            arr[a]=temp[a];
        }
        return score;
    }
    public static String textGenerate(int[] score){
        String str="";
        if (score[0]==4){
            str="Вы отгадали число! 4 очка в Вашу пользу! Если хотите сыграть еще раз, напищите /start";
        } else {
            if (score[0]==0){
                str="Как-то вообще не попали... У Вас 0 совпадений. Попробуйте снова.";
            } else {
                if (score[0]==1){
                    str="Маловато... Попробуйте снова.";
                } else {
                    if (score[0]==3){
                        str="Почти! Еще одна попытка!";
                    } else {
                        str="У Вас есть все шансы! Попробуйте снова!";
                    }
                }
            }
        }
        return str;
    }
    public static int[] cheat(int[] arr, int[] coincid){
        int[] changed=new int[4];
        for (int i=0;i<4;i++){
            if (coincid[i]==1){
                changed[i]=arr[i];
            } else {
                if (i==0){
                    changed[i]=1+(int)(Math.random()*9);
                } else{
                    changed[i]=(int) (Math.random() * 10);
                }
            }
        }
        return changed;
    }
}