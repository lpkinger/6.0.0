/**
 * 客户服务主页
 * */
Ext.QuickTips.init();
Ext.define('erp.controller.opensys.Default', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    views: ['opensys.default.Header','opensys.default.Center','opensys.default.Footer','opensys.default.Header','opensys.default.NavigationPanel'
    ],
    init: function() {
    	
    }
});