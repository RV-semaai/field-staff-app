package com.semaai.agent.model.newcustomer;

import java.io.Serializable;

public class CustomerRegisterModel implements Serializable {
        private String UserName;
        private String UserPhone;

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getUserPhone() {
            return UserPhone;
        }

        public void setUserPhone(String userPhone) {
            UserPhone = userPhone;
        }

}
