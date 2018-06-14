package com.bytelogs.photoblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter <PostAdapter.PostHolder>{

    List<PostPojo> postPojos;
    Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String uid;

    public PostAdapter(Context context, List<PostPojo> postPojos) {
        this.context = context;
        this.postPojos = postPojos;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, int position) {
        holder.setIsRecyclable(false);
        final PostPojo postPojo = postPojos.get(position);
        Glide.with(context).load(postPojo.getImage_url()).into(holder.postimg);
        long millisecTime = postPojo.getTime_stamp().getTime();
        String str = (String) DateUtils.getRelativeDateTimeString(context,
                millisecTime,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0);
        holder.timestamp.setText(str);
        holder.des.setText(postPojo.getDescription());
        Log.d("imgurl",postPojo.getImage_url());
        firebaseFirestore.collection("Users").document(postPojo.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String imageuserurl = task.getResult().getString("pro_image_url");

                    String name = task.getResult().getString("name");
                    holder.username.setText(name);
                    Glide.with(context).load(imageuserurl).into(holder.userimg);
                }
            }
        });
        firebaseFirestore.collection("Posts/"+postPojo.getPostid()+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    holder.liketext.setText(documentSnapshots.size()+" Likes");
                }else {
                    holder.liketext.setText("0 Likes");
                }
            }
        });
        firebaseFirestore.collection("Posts/"+postPojo.getPostid()+"/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    holder.commenttext.setText(documentSnapshots.size()+" Comment ");
                }else {
                    holder.commenttext.setText("0 Comments ");
                }
            }
        });
        firebaseFirestore.collection("Posts/"+postPojo.getPostid()+"/Likes").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                      holder.likebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                }else {
                      holder.likebtn.setImageResource(R.drawable.ic_thumb_up_gray_24dp);
                }
            }
        });

        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String  uid = firebaseAuth.getCurrentUser().getUid();
                firebaseFirestore.collection("Posts/"+postPojo.getPostid()+"/Likes").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String,Object> likesmap = new HashMap<>();
                            likesmap.put("time_stamp", FieldValue.serverTimestamp());
                            firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("Posts/"+postPojo.getPostid()+"/Likes").document(uid).set(likesmap);
                        }else {
                            firebaseFirestore.collection("Posts/"+postPojo.getPostid()+"/Likes").document(uid).delete();

                        }
                    }
                });



            }
        });
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,CommentActivity.class);
                intent.putExtra("POSTID",postPojo.getPostid());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return postPojos.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder{

        ImageView postimg,likebtn;
        CircleImageView userimg;
        TextView username,des,timestamp,liketext,commenttext;
        LinearLayout commentLayout;

        public PostHolder(View itemView) {
            super(itemView);

            postimg = itemView.findViewById(R.id.postimg);
            userimg = itemView.findViewById(R.id.circleImageView);
            username = itemView.findViewById(R.id.usertv);
            des = itemView.findViewById(R.id.destv);
            timestamp = itemView.findViewById(R.id.timestamp);
            likebtn = itemView.findViewById(R.id.likebtn);
            liketext = itemView.findViewById(R.id.liketext);
            commentLayout = itemView.findViewById(R.id.commecntLinearLayout);
            commenttext = itemView.findViewById(R.id.commentstext);
        }
    }
}
