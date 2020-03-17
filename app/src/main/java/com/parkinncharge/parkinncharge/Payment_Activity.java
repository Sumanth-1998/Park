package com.parkinncharge.parkinncharge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Payment_Activity extends AppCompatActivity implements PaymentResultListener {
    Checkout checkout;
    double amount;
    String type;
    Order order;
    RazorpayClient razorpay;
    String order_id="";
    String uid,time,booked_date;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_);
        db= FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        amount=intent.getDoubleExtra("amount",0);
        type=intent.getStringExtra("type");
        booked_date=intent.getStringExtra("booked_date");
        time=intent.getStringExtra("time");
        Log.d("Amount",amount+"");

        checkout=new Checkout();
        checkout.setKeyID("rzp_test_Xk70LwijqMfKDb");
        Checkout checkout = new Checkout();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /**
         * Set your logo here
         */
        //checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            if (user != null) {
                uid = user.getUid();
                String time = Long.toString(System.currentTimeMillis());
                String msg = uid + time;
                order_id = toHexString(getSHA(msg)).substring(0, 6).toUpperCase();

                JSONObject options = new JSONObject();
                razorpay = new RazorpayClient("rzp_test_Xk70LwijqMfKDb", "dOt8PN8oCqPR9WzuraOOrIIl");
                /**
                 * Merchant Name
                 * eg: ACME Corp || HasGeek etc.
                 */
                options.put("name", "ParkInnCharge");

                /**
                 * Description can be anything
                 * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
                 *     Invoice Payment
                 *     etc.
                 */
                options.put("description", "Order ID: " + order_id);
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                //options.put("order_id", "order_9A33XWu170gUtm");
                options.put("currency", "INR");

                /**
                 * Amount is always passed in currency subunits
                 * Eg: "500" = INR 5.00
                 */
                options.put("amount", amount);
                options.put("payment_capture", true);


                checkout.open(activity, options);
            }
        } catch(Exception e) {
            Log.e("Error","Error in payment",e);
        }


    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();

        Map<String,Object> book_details=new HashMap<>();
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        book_details.put("Date Of Booking",date);
        book_details.put("Status","Active");
        book_details.put("Type",type);
        book_details.put("Start Date",booked_date);
        book_details.put("Start Time",time);

        db.collection("booking").document(uid).collection("let out").document(order_id).set(book_details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setContentView(R.layout.payment_success);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Payment_Activity.this, "Could not enter into Firestore", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment failed:"+s, Toast.LENGTH_SHORT).show();

    }
}
