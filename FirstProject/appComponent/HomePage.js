/**
 * Created by lijie on 16/7/13.
 */

import React, { Component } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Dimensions,
    ScrollView,
    Image,
    RefreshControl
} from 'react-native';

import CommonComponent from './CommonComponent';
import Banner from 'react-native-banner';
import RoundImageView from './RoundedImageView';
const deviceWidth = Dimensions.get('window').width;
const deviceHeight=Dimensions.get('window').height;
import PtrFrameLayout from './PtrFrameLayout';

let bannerData=[];
let quickButton=[];
let columnData=[];

const DataState = {
    pending: 0,
    hasReached:1
};

const KEY_SCROLL_VIEW="scroll_view";

var self;

let hasRender=false;

const KEY_REFRESH="key_refresh";
export default class HomePage extends Component {

    // 构造
      constructor(props) {
        super(props);
        // 初始状态
        this.state = {
            defaultIndex: 0,
            dataState:DataState.pending,
            isRefreshing:false,
        };
          this.getRemoteData=this.getRemoteData.bind(this);
          this.parseData=this.parseData.bind(this);
          this.generateRootView=this.generateRootView.bind(this);
          this.newNumFormat=this.newNumFormat.bind(this);
          this.generateRoom=this.generateRoom.bind(this);
          this._onRefresh=this._onRefresh.bind(this);
      }


    newNumFormat(num){
        let numDouble;
        if (num>10000){
            numDouble=(num/10000).toFixed(1)+"万";
        }
        else if(num>1000000){
            numDouble=(num/1000000).toFixed(1)+"百万";
        }
        else{
            numDouble=num.valueOf();
        }
        return numDouble;
    }


    parseData(responseObj){
        if(responseObj){
            //处理columns
            responseObj.data.columns.forEach((item,index,arrays)=>{
                let channelsText=item.channelsText;
                let iconImg=item.game.icon;
                let name=item.game.name;
                let rooms=[];
                item.rooms.forEach((child,pos,as)=>{
                    let previewImg=child.preview;
                    let tag=child.channel.tag;
                    let channelName=child.channel.name;
                    let channelStatus=child.channel.status;
                    let viewers=this.newNumFormat(child.viewers);
                    rooms.push({
                        'previewImg':previewImg,
                        'tag':tag,
                        'channelName':channelName,
                        'channelStatus':channelStatus,
                        'viewers':viewers
                    })
                });
                columnData.push({
                    'channelsText':channelsText,
                    'iconImg':iconImg,
                    'name':name,
                    'rooms':rooms
                });
            });
            //处理quickButton
            responseObj.data.quickbutton.forEach((item,index,arrays)=>{
                quickButton.push(item.image);
            });
            //处理banner
            responseObj.data.banner.forEach((item,index,arrays)=>{
                let bannerImage=item.image;
                let bannerTitle=item.title;
                bannerData.push({
                    'image':bannerImage,
                    'title':bannerTitle
                });
                if(index===(arrays.length-1)){
                    this.setState({
                        dataState:DataState.hasReached
                    });
                    this.refs[KEY_REFRESH].stopRefresh();
                }
            });
        }
    }

    clickListener(index) {

    }

    onMomentumScrollEnd(event, state) {
        this.defaultIndex = state.index;
    }


    getRemoteData(){
        fetch('https://a4.plu.cn/api/home/personal?version=3.5&device=4',{
            method:'get'
        }).then((response)=>{
            if (response.ok){
                response.json().then((data)=>{
                    this.parseData(data);
                }).catch((err)=>{
                    console.log("err----"+err);
                });
            }
        }).catch((error)=>{
            console.log("error---"+error);
        });
    }

    componentWillMount() {
        self=this;
        this.getRemoteData();
    }

    generateRoom(itemColumn){
        let roomsLine=[];
        for (let i=0;i<itemColumn.rooms.length/2;i++){
            let firstStream=itemColumn.rooms[i*2];
            let lastStream=itemColumn.rooms[i*2+1];
            let mColor='#00AECB';
            if(i===0){
                mColor='#FF3A6E';
            }
            let tag1,tag2;
            if (itemColumn.name==='正在直播'){
                if(firstStream.tag){
                    tag1=<View style={{position: 'absolute',right:0,top:0,backgroundColor:mColor,paddingLeft:4,paddingRight:4}}>
                        <Text style={{fontSize:12,color:'white'}}>
                            {firstStream.tag}
                        </Text>
                    </View>;
                }
                else if(lastStream.tag){
                    tag2=<View style={{position: 'absolute',right:0,top:0,backgroundColor:mColor,paddingLeft:4,paddingRight:4}}>
                        <Text style={{fontSize:12,color:'white'}}>
                            {lastStream.tag}
                        </Text>
                    </View>;
                }
            }
            roomsLine.push(
                <View style={{flex:1,flexDirection:'row',justifyContent:'center',alignItems:'center'}} key={i}>
                    <View style={{flex:1,justifyContent:'center',alignItems:'center'}}>
                        <Image
                            style={{width:deviceWidth/2-10,height:96}}
                            source={{uri:firstStream.previewImg}}
                            resizeMode={Image.resizeMode.cover}
                        >
                            {tag1}
                            <Image
                                style={{width:deviceWidth/2-10,height:20,position:'absolute',bottom:0,flexDirection:'row',alignItems:'center'}}
                                resizeMode={Image.resizeMode.cover}
                                source={{uri:'img_grey1'}}
                            >
                                <Text
                                    numberOfLines={1}
                                    style={{color:'white',flex:1,fontSize:10,marginLeft:4}}>{firstStream.channelName}</Text>

                                <Image style={{width:10,height:10}}
                                       source={{uri:'icon_viewer'}}
                                       resizeMode={Image.resizeMode.cover}
                                />
                                <Text style={{color:'white',fontSize:10,marginRight:4}}>
                                    {firstStream.viewers}
                                </Text>
                            </Image>
                        </Image>
                        <Text style={{margin:4,fontSize:12}} numberOfLines={1}>
                            {firstStream.channelStatus}
                        </Text>
                    </View>
                    <View style={{flex:1,justifyContent:'center',alignItems:'center'}}>
                        <Image
                            style={{width:deviceWidth/2-10,height:96}}
                            source={{uri:lastStream.previewImg}}
                            resizeMode={Image.resizeMode.contain}
                        >
                            {tag2}
                            <Image
                                style={{width:deviceWidth/2-10,height:20,position:'absolute',bottom:0,flexDirection:'row',alignItems:'center'}}
                                resizeMode={Image.resizeMode.cover}
                                source={{uri:'img_grey1'}}
                            >
                                <Text
                                    numberOfLines={1}
                                    style={{color:'white',flex:1,fontSize:10,marginLeft:4}}>{lastStream.channelName}</Text>

                                <Image style={{width:10,height:10}}
                                       source={{uri:'icon_viewer'}}
                                       resizeMode={Image.resizeMode.cover}
                                />
                                <Text style={{color:'white',fontSize:10,marginRight:4}}>
                                    {lastStream.viewers}
                                </Text>
                            </Image>
                        </Image>
                        <Text style={{margin:4,fontSize:12}} numberOfLines={1}>
                            {lastStream.channelStatus}
                        </Text>
                    </View>
                </View>
            );
        }
        return (
            <View style={{backgroundColor:'white',marginTop:8,marginBottom:8}} key={itemColumn.name}>
                <View style={{flexDirection:'row',alignItems:'center',marginLeft:6,marginRight:6,paddingTop:6,paddingBottom:6}}>
                    <Image
                        style={{width:22,height:22}}
                        source={{uri:itemColumn.iconImg}}
                        resizeMode={Image.resizeMode.contain}
                    />
                    <Text style={{flex:1,marginLeft:10,fontSize:16}}>{itemColumn.name}</Text>
                    <Text style={{fontSize:13,color:'#777'}}>{itemColumn.channelsText}</Text>
                    <Image
                        style={{width:10,height:10,marginLeft:4}}
                        source={{uri:'ic_arrow_go'}}
                        resizeMode={Image.resizeMode.contain}
                    />
                </View>
                {roomsLine}
            </View>
        );
    }

    _onRefresh(){
        bannerData=[];
        quickButton=[];
        columnData=[];
        this.getRemoteData();
    }


    generateRootView(){
        let qb=[];
        quickButton.forEach((item,index,arrays)=>{
            qb.push(
                <RoundImageView url={item} style={{width:Dimensions.get('window').width/4,marginLeft:4,marginRight:4,marginTop:6}} key={index} />
            );
        });
        let cr=[];
        columnData.forEach((item,index,arrays)=>{
            cr.push(this.generateRoom(item));
        });
        return (
            <View style={styles.container}>
                <PtrFrameLayout
                    ref={KEY_REFRESH}
                    doRefresh={this._onRefresh}
                    style={{flex:1,backgroundColor:'#F1F1F1'}}>
                    <View style={{flex:1,backgroundColor: '#F1F1F1'}}>
                        <Banner
                            banners={bannerData}
                            defaultIndex={this.defaultIndex}
                            onMomentumScrollEnd={this.onMomentumScrollEnd.bind(this)}
                            intent={this.clickListener.bind(this)}
                            style={{height:140}}
                        />
                        <ScrollView
                            horizontal={true}
                            style={{height:60}}
                            >
                            <View style={{flexDirection:'row'}}>
                                {qb}
                            </View>
                        </ScrollView>
                        {cr}
                    </View>
                </PtrFrameLayout>
            </View>
        );
    }

    render(){
        let cp;
        switch (this.state.dataState){
            case DataState.hasReached:
                cp = this.generateRootView();
                break;
            default:
                cp=CommonComponent.renderLoadingView();
                break;
        }
        return cp;
    }
}

let styles=StyleSheet.create({
    container:{
        flex:1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF'
    }
});