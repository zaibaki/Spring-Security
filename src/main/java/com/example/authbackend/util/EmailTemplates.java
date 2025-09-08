package com.example.authbackend.util;

public class EmailTemplates {
    
    public static String getEmailVerificationTemplate(String firstName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verification</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">Email Verification</h1>
                </div>
                
                <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.08);">
                    <h2 style="color: #333; margin-top: 0;">Hello %s!</h2>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        Thank you for registering with our application. To complete your registration and activate your account, please verify your email address by clicking the button below.
                    </p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" 
                           style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                                  color: white; 
                                  padding: 15px 30px; 
                                  text-decoration: none; 
                                  border-radius: 5px; 
                                  font-weight: bold; 
                                  font-size: 16px;
                                  display: inline-block;
                                  transition: transform 0.2s;">
                            Verify Email Address
                        </a>
                    </div>
                    
                    <p style="font-size: 14px; color: #666; margin-top: 25px;">
                        If the button doesn't work, you can copy and paste this link into your browser:
                    </p>
                    <p style="font-size: 14px; color: #667eea; word-break: break-all;">
                        %s
                    </p>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;">
                    
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        This verification link will expire in 24 hours. If you didn't create an account with us, please ignore this email.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(firstName, verificationUrl, verificationUrl);
    }
    
    public static String getPasswordResetTemplate(String firstName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">Password Reset</h1>
                </div>
                
                <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.08);">
                    <h2 style="color: #333; margin-top: 0;">Hello %s!</h2>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        We received a request to reset your password. If you made this request, click the button below to reset your password.
                    </p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" 
                           style="background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%); 
                                  color: white; 
                                  padding: 15px 30px; 
                                  text-decoration: none; 
                                  border-radius: 5px; 
                                  font-weight: bold; 
                                  font-size: 16px;
                                  display: inline-block;
                                  transition: transform 0.2s;">
                            Reset Password
                        </a>
                    </div>
                    
                    <p style="font-size: 14px; color: #666; margin-top: 25px;">
                        If the button doesn't work, you can copy and paste this link into your browser:
                    </p>
                    <p style="font-size: 14px; color: #ff6b6b; word-break: break-all;">
                        %s
                    </p>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;">
                    
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        This password reset link will expire in 1 hour. If you didn't request a password reset, please ignore this email or contact support if you have concerns.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(firstName, resetUrl, resetUrl);
    }
    
    public static String getWelcomeTemplate(String firstName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Welcome</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">Welcome!</h1>
                </div>
                
                <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.08);">
                    <h2 style="color: #333; margin-top: 0;">Welcome %s!</h2>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        Your email has been successfully verified and your account is now active! 🎉
                    </p>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        You can now enjoy all the features of our application. We're excited to have you as part of our community!
                    </p>
                    
                    <div style="background: #e8f5e8; padding: 20px; border-radius: 5px; margin: 25px 0;">
                        <h3 style="color: #27ae60; margin-top: 0;">What's next?</h3>
                        <ul style="color: #666; margin: 0; padding-left: 20px;">
                            <li>Complete your profile</li>
                            <li>Explore our features</li>
                            <li>Connect with other users</li>
                        </ul>
                    </div>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;">
                    
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        Thank you for choosing our service. If you have any questions, feel free to contact our support team.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(firstName);
    }
}