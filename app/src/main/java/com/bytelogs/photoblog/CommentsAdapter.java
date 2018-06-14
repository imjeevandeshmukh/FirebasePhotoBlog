package com.bytelogs.photoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder>{
    List<CommentPojo> commentPojos;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Context context;
    public CommentsAdapter(List<CommentPojo> commentPojos) {
        this.commentPojos = commentPojos;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull

    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);

        return new CommentsAdapter.CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentHolder holder, int position) {
        CommentPojo commentPojo = commentPojos.get(position);
        firebaseFirestore.collection("Users").document(commentPojo.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String imageuserurl = task.getResult().getString("pro_image_url");
                    String name = task.getResult().getString("name");
                    holder.usernametv.setText(name);
                    Glide.with(context).load(imageuserurl).into(holder.userimage);
                }
            }
        });
        holder.commenttv.setText(commentPojo.getMessage());

    }

    @Override
    public int getItemCount() {
        return commentPojos.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder{

        CircleImageView userimage;
        TextView usernametv,commenttv;

        public CommentHolder(View itemView) {
            super(itemView);
            userimage = itemView.findViewById(R.id.circleImageView);
            usernametv = itemView.findViewById(R.id.usernametv);
            commenttv = itemView.findViewById(R.id.commenttv);
        }
    }
}
