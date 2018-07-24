/**
 * 客户服务桌面
 * */
Ext.QuickTips.init();
Ext.define('erp.controller.opensys.Home', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    views: ['opensys.home.InfoPanel','opensys.home.ProblemPanel'
    ],
    init: function() {
    }
});