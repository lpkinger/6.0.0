Ext.define('erp.view.ma.sql.ResultTab', {
    extend: 'Ext.tab.Panel',
    xtype: 'resulttab',
    id:'resulttab',
    controller: 'tab-view',
    requires: [
        'erp.view.ma.sql.TabController',
        'erp.view.ma.sql.Grid'
    ],
    defaults: {
        scrollable: true
    },
    items: [{
        title: '脚本输出',
        id: 'detail',
        region: 'center',
        padding: 7,
        bodyStyle: "background: #ffffff;",
        html: '查看执行结果!',
        tpl:new Ext.XTemplate(
        	    '<div>执行结果: ',        	       
        	    '<tpl if="error">', 
        	        '<font  color="red">失败！</font></div>' ,
                    '<p>错误原因: {error}</p>',               
                '<tpl else>',
                    '<font  color="red">成功！</font></div>' ,                  
               '</tpl>'
        	 )
    }, {
        title: '查询结果',
        xtype:'result-grid'        
    }]
});