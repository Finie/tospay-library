package net.tospay.auth.databinding;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.remote.Resource;
import net.tospay.auth.ui.base.BaseAdapter;

import java.util.List;


public final class ListBindingAdapter {
    @BindingAdapter(value = "resource")
    public static void setResource(RecyclerView recyclerView, Resource resource) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null)
            return;

        if (resource == null || resource.data == null)
            return;

        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).setData((List) resource.data);
        }
    }
}
