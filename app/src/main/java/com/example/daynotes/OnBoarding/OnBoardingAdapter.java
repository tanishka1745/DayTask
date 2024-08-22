package com.example.daynotes.OnBoarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.daynotes.R;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.ViewHolder> {

    List<OnBoardingItem> list;

    public OnBoardingAdapter(List<OnBoardingItem>list)
    {
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boarding,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OnBoardingItem onBoardingItem= list.get(position);
        //holder.title1.setText(onBoardingItem.getTitle());
        //holder.descrption1.setText(onBoardingItem.getDescription());
        holder.picture.setImageResource(onBoardingItem.getImage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView title1,descrption1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture=itemView.findViewById(R.id.onboardingimage);
            //title1=itemView.findViewById(R.id.textTitle);
            //descrption1=itemView.findViewById(R.id.textViewDescription);
        }
    }
}
