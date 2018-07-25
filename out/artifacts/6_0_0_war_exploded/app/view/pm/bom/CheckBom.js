Ext.define('erp.view.pm.bom.CheckBom',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 20%',
					id:'formPanel',
					enableTools: false
				},{
				xtype: 'grid',
				id: 'bom-check',
				anchor: '100% 80%',
				columns: [{
					text: '',
					dataIndex: 'check',
					flex: 1,
					renderer: function(val, meta, record) {
						meta.tdCls = val;
						return '';
					}
				},{
					text: '检测项',
					dataIndex: 'VALUE',
					flex: 10,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.style = 'color: gray';
						}
						return val;
					}
				},{
					text: '',
					dataIndex: 'link',
					flex: 1,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.tdCls = 'detail';
							return '详细情况';
						}
						return '';
					}
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'TYPE', type: 'string'}, {name: 'VALUE', type: 'string'}],			  
                    proxy: {
                     type: 'ajax',
                      url:basePath+'pm/bomCheck/getBomCheckItems.action?caller=BomCheck',
                      reader: {  
                           //数据格式为json  
                             type: 'json',
                             root: 'data'
                     }				
                    },
                 autoLoad:true
			  })
			}] 
		}); 
		me.callParent(arguments); 
	} 
});