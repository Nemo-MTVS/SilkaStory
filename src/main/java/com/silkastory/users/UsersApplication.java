package com.silkastory.users;

import java.util.Scanner;

public class UsersApplication {

    private final UsersService usersService;

    public UsersApplication(UsersService usersService, UsersService usersService1){
        this.usersService = usersService1;
    }

    // 회원가입
    public void saveUser() throws IllegalArgumentException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("==== 회원가입 ====");
        System.out.println("이름를 입력하세요.");
        String name = scanner.nextLine();

        System.out.print("닉네임을 입력하세요: ");
        String nickname = scanner.nextLine();

        usersService.saveUser(name, nickname);
        scanner.close();
    }

    public void updateUser(String id){
        Scanner scanner = new Scanner(System.in);
        System.out.print("새로운 닉네임을 입력하세요: ");
        String newNickname = scanner.nextLine();

        System.out.print("새로운 이름을 입력하세요: ");
        String newName = scanner.nextLine();
        boolean result = usersService.updateUser(id, newName, newNickname);;

        if (result) {
            System.out.println("사용자 정보가 성공적으로 업데이트되었습니다!");
        } else {
            System.out.println("사용자 정보 업데이트에 실패했습니다.");
        }
        scanner.close();
    }



}






