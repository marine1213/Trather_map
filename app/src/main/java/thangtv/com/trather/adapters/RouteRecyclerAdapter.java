package thangtv.com.trather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import thangtv.com.trather.R;
import thangtv.com.trather.app.MainApplication;
import thangtv.com.trather.ui.element.ProfileImageVIew;

/**
 * Created by Nguyen on 10/15/2015.
 */
public class RouteRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mItemList;
    private ImageLoader imageLoader;

    public RouteRecyclerAdapter(List<String> itemList) {
        mItemList = itemList;
        imageLoader = MainApplication.getInstance().getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_route_recycler_item, parent, false);
        return  new RecyclerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        holder.setItemText
                ("http://s120.avatar.zdn.vn/3/d/c/a/878118500_120_93.jpg");
//        String itemText = mItemList.get(position);
//        holder.setItemText(itemText);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
        private ProfileImageVIew mProfileImageVIew;

        public RecyclerItemViewHolder(final View parent) {
            super(parent);
            mProfileImageVIew = (ProfileImageVIew) parent.findViewById(R.id.im_acc_icon);

        }
        public void setItemText(String url) {
            mProfileImageVIew.setImageUrl(url,imageLoader);
        }
    }
}
