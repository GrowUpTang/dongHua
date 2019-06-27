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
 * ����ѡ��Dialog
 * Created by William on 2016/7/27.
 */
public class DialogAreaChose extends Dialog implements View.OnClickListener, OnWheelScrollListener {

    private PickerScrollView scrollProvince, scrollCity, scrollDistrict;
    private Context context;
    private String[] mProvinceDatas;//����ʡ
    private AreaChoseListener areaChoseListener;
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();//key - ʡ value - ��
    private Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>(); //key - �� values - ��
    private String mCurrentProviceName;//��ǰʡ������
    private String mCurrentCityName;//��ǰ�е�����
    private String mCurrentDistrictName = "";//��ǰ��������

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
            window.setGravity(Gravity.BOTTOM);//����Dialog�ڵײ���ʾ
            window.setBackgroundDrawableResource(android.R.color.transparent);//���ñ���͸��
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//���ú���ȫ��
            window.setWindowAnimations(R.style.dialog_share);
        }

        initProvinceDatas();//��ʼ��������Ϣ
        init();//��ʼ��
    }

    private void init() {
        TextView tvBack = (TextView) findViewById(R.id.area_chose_cancel);
        TextView tvFinish = (TextView) findViewById(R.id.area_chose_finish);
        scrollProvince = (PickerScrollView) findViewById(R.id.area_chose_province);
        scrollCity = (PickerScrollView) findViewById(R.id.area_chose_city);
        scrollDistrict = (PickerScrollView) findViewById(R.id.area_chose_district);

        scrollProvince.setViewAdapter(new ArrayWheelAdapter<String>(
                context, mProvinceDatas));
        // ���ÿɼ���Ŀ����
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
            case R.id.area_chose_cancel:    //ȡ��
                this.dismiss();
                break;
            case R.id.area_chose_finish:    //���
                areaChoseListener.onClick(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName);
                this.dismiss();
                break;
        }
    }

    //��������XMl�ļ�
    private void initProvinceDatas() {
        List<ProvinceModel> provinceList;
        AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open("area_data.xml");
            // ����һ������xml�Ĺ�������
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // ����xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // ��ȡ��������������
            provinceList = handler.getDataList();
            //*/ ��ʼ��Ĭ��ѡ�е�ʡ���С���
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
                    // ��������ʡ������
                    mProvinceDatas[i] = provinceList.get(i).getName();
                    List<CityModel> cityList = provinceList.get(i).getCityList();
                    String[] cityNames = new String[cityList.size()];
                    for (int j = 0; j < cityList.size(); j++) {
                        // ����ʡ����������е�����
                        cityNames[j] = cityList.get(j).getName();
                        List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                        String[] distrinctNameArray = new String[districtList.size()];
//                        DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                        for (int k = 0; k < districtList.size(); k++) {
                            // ����������������/�ص�����
                            DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                            // ��/�ض��ڵ��ʱ࣬���浽mZipcodeDatasMap
//                            distrinctArray[k] = districtModel;
                            distrinctNameArray[k] = districtModel.getName();
                        }
                        // ��-��/�ص����ݣ����浽mDistrictDatasMap
                        mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                    }
                    // ʡ-�е����ݣ����浽mCitisDatasMap
                    mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ���ݵ�ǰ���У�������WheelView����Ϣ
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
     * ���ݵ�ǰ��ʡ��������WheelView����Ϣ
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
