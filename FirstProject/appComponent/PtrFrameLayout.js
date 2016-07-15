/**
 * Created by lijie on 16/7/13.
 */
'use strict';

const React = require('React');
const ReactNative = require('ReactNative');
const requireNativeComponent = require('requireNativeComponent');
const View = require('View');
const Text = require('Text');
const Dimensions=require('Dimensions');
const deviceWidth = Dimensions.get('window').width;
const ScrollView =require('ScrollView');
var UIManager = require('UIManager');
const PK_REF_KEY="pk_ref_key";
const PtrFrameLayout =React.createClass({
    propTypes: {
        ...View.propTypes,
    },

    generatedContent:function () {
      return (
          <ScrollView style={{width:deviceWidth,height:300,backgroundColor:'white'}} >
              {this.props.children}
          </ScrollView>
      );
    },
    stopRefresh:function () {
        UIManager.dispatchViewManagerCommand(
            this.getPluImageHandle(),
            1,
            null
        );
    },
    getPluImageHandle: function() {
        return ReactNative.findNodeHandle(this.refs[PK_REF_KEY]);
    },
    render:function () {
        return (
            <AndroidPtrFrameLayout
                ref={PK_REF_KEY}
                onRefresh={()=>{
                    this.props.doRefresh&&this.props.doRefresh();
                }}
                {...this.props} >
                {this.generatedContent()}
            </AndroidPtrFrameLayout>
        );
    }
});

let AndroidPtrFrameLayout=requireNativeComponent('PtrFrameLayout',PtrFrameLayout,{});
module.exports=PtrFrameLayout;