/**
 * Created by lijie on 16/7/13.
 */
'use strict';

import { PropTypes } from 'react';
import { requireNativeComponent, View } from 'react-native';

var iface = {
    name: 'RoundImageView',
    propTypes: {
        ...View.propTypes,
        url: PropTypes.string,
    },
};

module.exports = requireNativeComponent('AndroidRoundImage', iface);