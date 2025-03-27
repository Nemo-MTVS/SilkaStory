package com.silkastory.users;

public class UsersService {

    private final UsersDao usersDao;


    public UsersService(UsersDao usersDao){
        this.usersDao = usersDao;
    }

    public void saveUser(String name, String nickname) throws IllegalArgumentException{
        validaInputString(name);
        validaInputString(nickname);

        usersDao.saveUser(name, nickname);
    }

    private void validaInputString(String str) throws IllegalArgumentException{
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("입력이 잘못되었습니다.");
        }
    }

    public boolean updateUser(String id, String newName, String newNickName) throws IllegalArgumentException{
        validaInputString(id);
        validaInputString(newName);
        validaInputString(newNickName);

        return usersDao.updateUser(id, newNickName, newName);
    }

}
