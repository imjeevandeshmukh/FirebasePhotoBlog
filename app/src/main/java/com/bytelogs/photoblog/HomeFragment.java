package com.bytelogs.photoblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    RecyclerView recyclerView;
    List<PostPojo> postPojoList;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    PostAdapter postAdapter;
    DocumentSnapshot lastVisible;
    Boolean isFirstPageLoaded = true;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerview);
        postPojoList = new ArrayList<>();
        if(firebaseAuth.getCurrentUser()!=null) {
            postAdapter = new PostAdapter(getContext(), postPojoList);
            firebaseFirestore = FirebaseFirestore.getInstance();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(postAdapter);
            Query firebaseQuery = firebaseFirestore.collection("Posts").orderBy("time_stamp", Query.Direction.DESCENDING).limit(3);
            firebaseQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (isFirstPageLoaded) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    }
                    for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            PostPojo postPojo = documentChange.getDocument().toObject(PostPojo.class);
                            postPojo.setPostid(documentChange.getDocument().getId());
                            if (isFirstPageLoaded) {
                                postPojoList.add(postPojo);
                            } else {
                                postPojoList.add(0, postPojo);
                            }

                            postAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageLoaded = false;

                }
            });
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedlast = !recyclerView.canScrollVertically(-1);
                    if (reachedlast) {

                        loadMore();
                    }
                }
            });
        }


        return view;
    }
    public void loadMore(){
        Query firebaseQuery = firebaseFirestore.collection("Posts").orderBy("time_stamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);
        firebaseQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            PostPojo postPojo = documentChange.getDocument().toObject(PostPojo.class);
                            postPojo.setPostid(documentChange.getDocument().getId());
                            postPojoList.add(postPojo);
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        });
    }

}
