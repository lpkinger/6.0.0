Ext.define('erp.view.plm.test.Pressure',{ 
	extend: 'Ext.Viewport', 
	layout: 'border',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpTreePanel',
				tbar: null,
				width: '25%',
				dockedItems: null
			},{
				xtype: 'panel',
				id: 'testpage',
				region: 'center',
				bodyStyle: 'background:#f1f1f1;',
				html: '<iframe id="iframe_test" src="' + basePath + 'resource/images/screens/noselect.png" height="100%" width="100%" frameborder="0" style="border-width: 0px;" scrolling="auto"></iframe>'
			},{
				xtype: 'form',
				id: 'testcontent',
				region: 'east',
				width: '25%',
				layout: 'column',
				bodyStyle: 'background:#f1f1f1;',
				items: [{
					xtype: 'displayfield',
					fieldLabel: '测试页面',
					labelWidth: 70,
					columnWidth: 1,
					id: 't_page',
					name: 't_page',
					fieldStyle: 'color:blue;'
				},{
					xtype: 'fieldcontainer',
					fieldLabel: '压力指数',
					labelWidth: 70,
					columnWidth: 1,
					layout: 'column',
					items: [{
						xtype: 'combobox',
						id: 't_count',
						editable: false,
						value: 10,
						columnWidth: 0.35,
						store: Ext.create('Ext.data.Store', {
				            fields: ['display', 'value'],
				            data : [
				                {"display": 10, "value": 10},
				                {"display": 20, "value": 20},
				                {"display": 50, "value": 50},
				                {"display": 100, "value": 100},
				                {"display": 200, "value": 200},
				                {"display": 500, "value": 500},
				                {"display": 1000, "value": 1000}
				            ]
				        }),
				        displayField: 'display',
				        valueField: 'value',
				        queryMode: 'local',
				        listeners: {
				        	change: function(f){
				        		switch(f.value){
					        		/*case 10:
					        			Ext.getCmp('t_level').setValue('※');break;
					        		case 20:
					        			Ext.getCmp('t_level').setValue('※ ※');break;
					        		case 50:
					        			Ext.getCmp('t_level').setValue('※ ※ ※');break;
					        		case 100:
					        			Ext.getCmp('t_level').setValue('※ ※ ※ ※');break;
					        		case 200:
					        			Ext.getCmp('t_level').setValue('※ ※ ※ ※ ※');break;
					        		case 1000:
					        			Ext.getCmp('t_level').setValue('※ ※ ※ ※ ※ ');break;*/
				        		}
				        	}
				        }
					},{
						xtype: 'displayfield',
						columnWidth: 0.65,
						value: '※',
						id: 't_level',
						fieldStyle: 'color:red;'
					}]
				},{
					xtype: 'button',
					text: '生成模拟数据',
					id: 't_input',
					columnWidth: 0.8,
					iconCls: 'x-button-icon-download',
				},{
					xtype: 'displayfield',
					columnWidth: 0.2,
					value: '※',
					id: 't_level1',
					fieldStyle: 'color:red;'
				},{
					xtype: 'button',
					text: '保存',
					id: 't_save',
					columnWidth: 1/3,
					iconCls: 'x-button-icon-save'
				},{
					xtype: 'button',
					text: '提交',
					disabled:true,
					id: 't_submit',
					columnWidth: 1/3,
					iconCls: 'x-button-icon-submit'
				},{
					xtype: 'button',
					text: '审核',
					disabled:true,
					id: 't_audit',
					columnWidth: 1/3,
					iconCls: 'x-button-icon-check'
				},{
					xtype: 'button',
					text: '清空结果',
					columnWidth: 0.5,
					iconCls: 'x-button-icon-delete',
					handler: function(btn){
						btn.ownerCt.down('#t_result').setValue('测试结果:');
					}
				},{
					xtype: 'button',
					text: '清除测试数据',
					columnWidth: 0.5,
					iconCls: 'x-button-icon-close',
					id: 't_clear'
				},{
					xtype: 'textarea',
					height: 480,
					columnWidth: 1,
					id: 't_result',
					value: '测试结果:',
					readOnly: true
				},{
					xtype: 'hidden',
					id: 'codeString'
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '分析测试结果',
					width: 100,
					cls: 'x-btn-gray',
					id: 't_analyse'
				},{
					text: '生成测试报告',
					width: 100,
					cls: 'x-btn-gray',
					id: 't_report'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});