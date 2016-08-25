package com.sevenheaven.uilibrarysample;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 7heaven on 16/8/25.
 */
public class ExampleListAdapter extends RecyclerView.Adapter<ExampleListAdapter.ExampleListViewHolder> {

    private List<ExampleItem> mExampleItemList;

    public ExampleListAdapter(List<ExampleItem> exampleItemList){
        mExampleItemList = exampleItemList;
    }

    @Override
    public int getItemCount(){
        return mExampleItemList.size();
    }

    @Override
    public ExampleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ExampleListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_examplelist, parent, false));
    }

    @Override
    public void onBindViewHolder(ExampleListViewHolder viewHolder, int position){
        final ExampleItem item = mExampleItemList.get(position);
        viewHolder.title.setText(item.title);
        if(item.provider != null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ExampleDetailActivity.class);
                    ExampleDetailActivity.mContentProvider = item.provider;
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    public static class ExampleListViewHolder extends RecyclerView.ViewHolder{
        TextView title;

        public ExampleListViewHolder(View itemView){
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public static class ExampleItem{
        String title;
        ExampleDetailActivity.DetailContentProvider provider;

        public ExampleItem(String title, ExampleDetailActivity.DetailContentProvider provider){
            this.title = title;
            this.provider = provider;
        }
    }
}
