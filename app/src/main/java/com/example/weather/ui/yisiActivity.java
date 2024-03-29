package com.example.weather.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.weather.R;

public class yisiActivity extends AppCompatActivity {
    //文件名称
    private final static String CityFileName1 = "天气隐私政策.txt";
    final String news = "<font color='black' size='48'>云皓天气APP隐私政策</font><br>" +
            "<font color='black' size='24'><br>深圳市辉皓达实业发展有限公司</font><br>" +
            "<font  size='24'><br>云皓天气APP尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更有个性化的服务，云皓天气APP会按照本隐私权政策的规定使用和披露您的个人信息。但监管信息平台将以高度的勤勉、审慎义务对待这些信息。除本隐私权政策另有规定外，在未征得您事先许可的情况下，监管信息平台不会将这些信息对外披露或向第三方提供云皓天气APP会不时更新本隐私权政策。 您在同意监管信息平台服务使用协议之时，即视为您已经同意本隐私权政策全部内容。本隐私权政策属于云皓天气APP服务使用协议不可分割的一部分。 </font><br>"
            + "<font color='black' size='24'><br>1. 适用范围 </font><br>"
            + "<font size='24'><br>a) 在您使用云皓天气APP网络服务，云皓天气APP平台自动接收并记录的您的浏览器和计算机上的信息，包括但不限于您的IP地址、浏览器的类型、使用的语言、访问日期和时间、软硬件特征信息及您需求的网页记录等数据；  </font><br>"
            + "<font size='24'><br>b) 云皓天气APP通过合法途径从商业伙伴处取得的用户个人数据。  </font><br>"
            + "<font size='24'><br>您了解并同意，以下信息不适用本隐私权政策： </font><br>"
            + "<font size='24'><br>a) 您在使用监管信息平台平台提供的搜索服务时输入的关键字信息； </font><br>"
            + "<font size='24'><br>b) 云皓天气APP收集到的您在云皓天气APP发布的有关信息数据，包括但不限于参与活动、成交信息及评价详情；</font><br>"
            + "<font size='24'><br>c) 违反法律规定或违反监管信息平台规则行为及监管信息平台已对您采取的措施。 </font><br>"
            + "<font color='black' size='24'><br>2. 信息使用  </font><br>"
            + "<font size='24'><br>a) 云皓天气APP不会向任何无关第三方提供、出售、出租、分享或交易您的个人信息，除非事先得到您的许可，或该第三方和监管信息平台（含云皓天气APP关联公司）单独或共同为您提供服务，且在该服务结束后，其将被禁止访问包括其以前能够访问的所有这些资料。  </font><br>"
            + "<font size='24'><br>b) 云皓天气APP亦不允许任何第三方以任何手段收集、编辑、出售或者无偿传播您的个人信息。任何云皓天气APP平台用户如从事上述活动，一经发现，云皓天气APP有权立即终止与该用户的服务协议。 \n </font><br>"
            + "<font color='black' size='24'><br>3. 信息披露  </font><br>"
            + "<font size='24'><br>在如下情况下，云皓天气APP将依据您的个人意愿或法律的规定全部或部分的披露您的个人信息：</font><br>"
            + "<font size='24'><br>a) 经您事先同意，向第三方披露；  </font><br>"
            + "<font size='24'><br>b) 为提供您所要求的产品和服务，而必须和第三方分享您的个人信息； </font><br>"
            + "<font size='24'><br>c) 根据法律的有关规定，或者行政或司法机构的要求，向第三方或者行政、司法机构披露； </font><br>"
            + "<font size='24'><br>d) 如您出现违反中国有关法律、法规或者云皓天气APP服务协议或相关规则的情况，需要向第三方披露； </font><br>"
            + "<font size='24'><br>e) 如您是适格的知识产权投诉人并已提起投诉，应被投诉人要求，向被投诉人披露，以便双方处理可能的权利纠纷； </font><br>"
            + "<font size='24'><br>f) 其它监管信息平台根据法律、法规或者网站政策认为合适的披露。 </font><br>"
            + "<font color='black' size='24'><br>4. 信息存储和交换   </font><br>"
            + "<font size='24'><br>云皓天气APP收集的有关您的信息和资料将保存在云皓天气APP及（或）其关联公司的服务器上，这些信息和资料可能传送至您所在国家、地区或监管信息平台收集信息和资料所在地的境外并在境外被访问、存储和展示。 \n </font><br>";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yisi);
        textView = findViewById(R.id.text);

        textView.setText(Html.fromHtml(news));//这是显示段落文本的,通过解析html
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());//段落文本的话要加这个
    }
}