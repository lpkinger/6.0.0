/**
 * DataList导出数据模板
 * 
 * @author yingp
 */
Ext.define('erp.view.core.window.DataTemplate', {
	extend: 'Ext.window.Window',
	alias: 'widget.datatemplate',
	title: '<font color=#CD6839>选择模板</font>',
	iconCls: 'x-button-icon-set',
	height: screen.height*0.6,
	width: screen.width*0.8,
    maximizable : true,
	buttonAlign : 'center',
	layout : 'anchor',
	caller : null,
	initComponent: function() {
		this.callParent(arguments);
		this.show();
		this.store = this.getDataTemplateStore(this.caller);
		this.add(this.createDataView(this.store));
	},
	createDataView: function(store){
		return Ext.create('Ext.view.View', {
			anchor: '100% 70%',
    		bodyStyle: 'background:#f1f1f1;',
	        deferInitialRefresh: false,
	        store : store,
	        tpl: Ext.create('Ext.XTemplate',
	        	'<tpl for=".">',
	                '<div class="phone">',
	                    (!Ext.isIE6? '<img width="64" height="64" src="images/phones/{[values.name.replace(/ /g, "-")]}.png" />' :
	                     '<div style="width:74px;height:74px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'images/phones/{[values.name.replace(/ /g, "-")]}.png\',sizingMethod=\'scale\')"></div>'),
	                    '<strong>{name}</strong>',
	                    '<span>{price:usMoney} ({reviews} Review{[values.reviews == 1 ? "" : "s"]})</span>',
	                '</div>',
	            '</tpl>'
	        ),
	        id: 'phones',
	        style: 'background:#f1f1f1;',
	        itemSelector: 'div.phone',
	        overItemCls : 'phone-hover',
	        multiSelect : true,
	        autoScroll  : true
		});
	},
	getDataTemplateStore : function(caller) {
		Ext.define('DataTemplate', {
	        extend: 'Ext.data.Model',
	        fields: [
	            {name: 'DT_CALLER', type: 'string'},
	            {name: 'DT_DESC', type: 'string'},
	            {name: 'DT_MAN', type: 'string'},
	            {name: 'DT_DATE', type: 'string'},
	            {name: 'DT_FIELDS', type: 'string'}
	        ]
		});
		return Ext.create('Ext.data.Store', {
			model: 'DataTemplate',
			proxy: {
		         type: 'ajax',
		         url: basePath + 'common/getFieldsDatas.action',
		         params: {
		        	 caller: 'DataTemplate',
		        	 fields: 'dt_fields,dt_desc,dt_man,to_char(dt_date,\'yyyy-mm-dd\')',
		        	 condition: 'dt_caller=\'' + caller + '\''
		         },
		         reader: {
		             type: 'json',
		             root: 'data'
		         }
		     },
		     autoLoad: true
		});
	}
});