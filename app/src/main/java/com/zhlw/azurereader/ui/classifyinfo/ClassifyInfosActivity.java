package com.zhlw.azurereader.ui.classifyinfo;

import android.os.Binder;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.custom.ShapeTextView;
import com.zhlw.azurereader.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ClassifyInfosActivity extends BaseActivity {

    @BindView(R.id.ll_title_back)
    LinearLayout llTitleBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.rv_bookshop_more)
    RecyclerView rvBookshopMore;

    private Unbinder binder;
    private ClassifyInfosActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshop_more);
        binder = ButterKnife.bind(this);
        presenter = new ClassifyInfosActivityPresenter(this);
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binder.unbind();
    }

    public LinearLayout getLlTitleBack() {
        return llTitleBack;
    }

    public TextView getTvTitleText() {
        return tvTitleText;
    }

    public RecyclerView getRvBookshopMore() {
        return rvBookshopMore;
    }

}
