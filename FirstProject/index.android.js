/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    View
} from 'react-native';

import ToolBar from './appComponent/Toolbar';
const Routes=require('./appComponent/Routes');
const ScrollableTabView = require('react-native-scrollable-tab-view');
import TabBar from './appComponent/TabBar';
import Constants from './appComponent/Constants';
import CodePush from 'react-native-code-push';
class FirstProject extends Component {

    // 构造
    constructor(props) {
        super(props);
        // 初始状态
        this.state = {
            selectedTab:0
        };
        this._onTabChanged=this._onTabChanged.bind(this);
    }

    _onTabChanged(index){
        if (this.state.selectedTab!==index.i){
            this.setState({
                selectedTab:index.i
            });
        }
    }

    componentWillMount() {
        CodePush.sync({
            deploymentKey: 'm-riu3kK1__kGboK4WPIwBYs7ZwzEygDCEa5e',
            updateDialog: {
                optionalIgnoreButtonLabel: '稍后',
                optionalInstallButtonLabel: '后台更新',
                optionalUpdateMessage: '是否更新？---',
                title: '更新提示'
            },
            installMode: CodePush.InstallMode.IMMEDIATE
        });
    }

    render() {
        return (
            <View style={styles.container}>
                <ToolBar  selectedTab={this.state.selectedTab} tabArray={Constants.TAB_ARRAYS} />
                <ScrollableTabView
                    renderTabBar={() => <TabBar />}
                    tabBarPosition={'bottom'}
                    locked={true}
                    onChangeTab={this._onTabChanged}
                    initialPage={0}
                >
                    {Routes.navigator('tab_1')}
                    {Routes.navigator('tab_2')}
                    {Routes.navigator('tab_3')}
                    {Routes.navigator('tab_4')}
                </ScrollableTabView>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F1F1F1'
    }
});

AppRegistry.registerComponent('FirstProject', () => FirstProject);
