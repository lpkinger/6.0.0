Ext.define('erp.view.oa.batchMail.mailSelect', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.erpMailSelectPanel',
	layout: 'accordion',
	defaults: {
        // applied to each contained panel
        bodyStyle: 'padding:5px'
    },
    layoutConfig: {
        // layout-specific configs go here
        titleCollapse: false,
        animate: true,
        activeOnTop: true
    },
    items: [{
    	xtype: 'orgSelectPanel',
    	title: '<font style="height:20px;line-height:20px;font-size:16px;font-weight:600">公司组织架构</font>'
    },{
    	title: '<font style="height:20px;line-height:20px;font-size:16px;font-weight:600">个人通讯组</font>',
    	xtype: 'personalGroupPanel'
    }]
});