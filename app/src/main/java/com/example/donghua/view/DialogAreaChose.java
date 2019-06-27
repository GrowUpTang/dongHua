package com.example.donghua.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.donghua.R;
import com.example.donghua.adapter.ArrayWheelAdapter;
import com.example.donghua.listener.OnWheelScrollListener;
import com.example.donghua.model.CityModel;
import com.example.donghua.model.DistrictModel;
import com.example.donghua.model.ProvinceModel;

/**
 * 区域选择Dialog
 * Created by William on 2016/7/27.
 */
public class DialogAreaChose extends Dialog implements View.OnClickListener, OnWheelScrollListener {

    private PickerScrollView scrollProvince, scrollCity, scrollDistrict;
    private Context context;
    private String[] mProvinceDatas;//所有省
    private AreaChoseListener areaChoseListener;
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();//key - 省 value - 市
    private Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>(); //key - 市 values - 区
    private String mCurrentProviceName;//当前省的名称
    private String mCurrentCityName;//当前市的名称
    private String mCurrentDistrictName = "";//当前区的名称

    public DialogAreaChose(Context context, AreaChoseListener areaChoseListener) {
        super(context);
        this.context = context;
        this.areaChoseListener = areaChoseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_area_chose);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);//设置Dialog在底部显示
            window.setBackgroundDrawableResource(android.R.color.transparent);//设置背景透明
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//设置横向全屏
            window.setWindowAnimations(R.style.dialog_share);
        }

        initProvinceDatas();//初始化地区信息
        init();//初始化
    }

    private void init() {
        TextView tvBack = (TextView) findViewById(R.id.area_chose_cancel);
        TextView tvFinish = (TextView) findViewById(R.id.area_chose_finish);
        scrollProvince = (PickerScrollView) findViewById(R.id.area_chose_province);
        scrollCity = (PickerScrollView) findViewById(R.id.area_chose_city);
        scrollDistrict = (PickerScrollView) findViewById(R.id.area_chose_district);

        scrollProvince.setViewAdapter(new ArrayWheelAdapter<String>(
                context, mProvinceDatas));
        // 设置可见条目数量
        scrollProvince.setVisibleItems(7);
        scrollCity.setVisibleItems(7);
        scrollDistrict.setVisibleItems(7);

        updateCities();
        updateAreas();

        scrollProvince.addScrollingListener(this);
        scrollCity.addScrollingListener(this);
        scrollDistrict.addScrollingListener(this);
        tvBack.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.area_chose_cancel:    //取消
                this.dismiss();
                break;
            case R.id.area_chose_finish:    //完成
                areaChoseListener.onClick(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName);
                this.dismiss();
                break;
        }
    }

    //解析区域XMl文件
    private void initProvinceDatas() {
        List<ProvinceModel> provinceList;
        AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open("area_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                }
            }
            if (provinceList != null) {
                mProvinceDatas = new String[provinceList.size()];
                for (int i = 0; i < provinceList.size(); i++) {
                    // 遍历所有省的数据
                    mProvinceDatas[i] = provinceList.get(i).getName();
                    List<CityModel> cityList = provinceList.get(i).getCityList();
                    String[] cityNames = new String[cityList.size()];
                    for (int j = 0; j < cityList.size(); j++) {
                        // 遍历省下面的所有市的数据
                        cityNames[j] = cityList.get(j).getName();
                        List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                        String[] distrinctNameArray = new String[districtList.size()];
//                        DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                        for (int k = 0; k < districtList.size(); k++) {
                            // 遍历市下面所有区/县的数据
                            DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                            // 区/县对于的邮编，保存到mZipcodeDatasMap
//                            distrinctArray[k] = districtModel;
                            distrinctNameArray[k] = districtModel.getName();
                        }
                        // 市-区/县的数据，保存到mDistrictDatasMap
                        mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                    }
                    // 省-市的数据，保存到mCitisDatasMap
                    mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = scrollCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        scrollDistrict
                .setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
        scrollDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = scrollProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        scrollCity.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
        scrollCity.setCurrentItem(0);
        updateAreas();
    }

    @Override
    public void onScrollingStarted(PickerScrollView wheel) {

    }

    @Override
    public void onScrollingFinished(PickerScrollView wheel) {
        if (wheel == scrollProvince) {
            updateCities();
        } else if (wheel == scrollCity) {
            updateAreas();
        } else if (wheel == scrollDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[wheel
                    .getCurrentItem()];
        }
    }

    public interface AreaChoseListener {
        void onClick(String privince, String city, String district);
    }

}
