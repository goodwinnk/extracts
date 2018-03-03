const webpack = require("webpack");
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    devtool: 'inline-source-map',
    entry: {
        popup: path.join(__dirname, 'src/popup.ts'),
        options: path.join(__dirname, 'src/options.ts'),
        content_script: path.join(__dirname, 'src/content_script.ts'),
        background: path.join(__dirname, 'src/background.ts'),
        vendor: ['another-rest-client', 'js-yaml', 'kotlin', 'core-js']
    },
    module: {
        loaders: [
            // {
            //     enforce: 'pre',
            //     test: /\.js$/,
            //     loader: "source-map-loader"
            // },
            {
                exclude: /node_modules/,
                test: /\.tsx?$/,
                loader: 'ts-loader'
            }
        ],
        },
    resolve: {
        extensions: ['.ts', '.tsx', '.js']
    },
    output: {
        path: path.resolve(__dirname, 'dist/js'),
        filename: '[name].js'
    },
    plugins: [
        new CopyWebpackPlugin([
            { from: '../../core-common/core-js/build/classes/kotlin/main', to: '../../node_modules/core-js' },
            { from: '../../core-common/core-js/build/resources/main', to: '../../node_modules/core-js' }
        ]),

        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor',
            minChunks: Infinity
        }),

        new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/),
    ]
};
