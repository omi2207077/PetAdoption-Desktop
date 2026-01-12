package com.example.pet_adoption_system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SignupController {

    private final DBHelper db;
    private final SignupUIInterface ui;


    public interface SignupUIInterface {
        void showLoading(boolean isLoading);
        void showMessage(String message);
        void onSignupSuccess();
    }

    public SignupController(SignupUIInterface ui, DBHelper db) {
        this.ui = ui;
        this.db = db;
    }

    public void handleSignup(String name, String phone, String password) {

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            ui.showMessage("Fill all fields!");
            return;
        }

        if (phone.length() < 10) {
            ui.showMessage("Invalid phone number");
            return;
        }

        if (password.length() < 4) {
            ui.showMessage("Password too short");
            return;
        }


        if (db.isPhoneExists(phone)) {
            ui.showMessage("This number is already registered");
            return;
        }


        ui.showLoading(true);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {

            db.registerUser(name, phone, password);


            ui.showLoading(false);
            ui.showMessage("Registration Successful");
            ui.onSignupSuccess();

            executor.shutdown();
        }, 1100, TimeUnit.MILLISECONDS);
    }
}