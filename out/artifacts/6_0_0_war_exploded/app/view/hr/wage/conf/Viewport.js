Ext.define('erp.view.hr.wage.conf.Viewport',{
	extend:'Ext.Viewport',
	layout:'anchor',
	id:'viewport',
	initComponent:function(){
		var me = this;
		Ext.apply(me,{
			items:[{
				xtype:'erpWageConfFormPanel',
				anchor:'100% 40%'
			},
			{
				xtype: 'tabpanel', 
				anchor: '100% 60%',
//				activeTab:0,
				id: 'mytab',
				items: [
				{
					title:"加班设置",
					layout: 'anchor',
					items:[{
						anchor: '100% 100%',
						keyField: 'WO_ID',
						caller:'WageOverWorkConf',
						xtype: 'erpOverWorkGridPanel'
					}]
				},{
					title:"缺勤设置",
					layout: 'anchor',
					items:[{
						anchor: '100% 100%',
						keyField: 'WAC_ID',
						caller:'WageAbsenceConf',
						xtype: 'erpAbsenceGridPanel'
					}]
				},{
					title:"个税设置",
					layout: 'anchor',
					items:[{
						anchor: '100% 100%',
						keyField: 'WP_ID',
						caller:'WagePersonTaxConf',
						xtype: 'erpPersonTaxGridPanel'
					}]
				}
				]
			}]
		})
		me.callParent(arguments)
	}
});