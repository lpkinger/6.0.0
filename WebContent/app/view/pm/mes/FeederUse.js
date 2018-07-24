Ext.define('erp.view.pm.mes.FeederUse',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%'
			},{
				xtype:'tabpanel',
				anchor: '100% 55%', 
				items:[{
					xtype: 'erpQueryGridPanel',
					title:'适用Feeder规格列表'
				},{
					title:'已领Feeder列表',
					items: [],
					layout: 'anchor',
					id: 'tab-list'
				}]
			},{
				xtype: 'form',
				anchor: '100% 25%',
				bodyStyle: 'background: #f1f1f1;',
				layout: 'border',
				items: [{
					xtype: 'fieldcontainer',
					region: 'center',
					autoScroll: true,
					scrollable: true,
					defaults: {
						width: 300
					},
					items: [{
						xtype: 'textfield',
						fieldLabel: '操作人员',
						readOnly:true,
						id:'man',
						allowBlank: false,
						value: em_name
					},{
						xtype: 'fieldcontainer',
						fieldLabel : '操作', 
						defaultType: 'radiofield',
						layout: 'hbox',
						defaults: {
			                flex: 1
			            },
			            items: [{
			            	boxLabel  : '领用',
			                inputValue: 'get',
			                name: 'operator',
			                id        : 'get',
			                checked: true
			            }, {
			                boxLabel  : '退回',
			                inputValue: 'back',
			                name: 'operator',
			                id        : 'back'
			            }]
					},{
						xtype: 'textareatrigger',
						fieldLabel: '原因',
						id:'reason'
					},{
						xtype: 'erpYnField',
						fieldLabel: '是否停用',						
						id:'isuse',
						value: 0
					},{
						xtype: 'textfield',
						fieldLabel: 'Feeder编号',
						readOnly:false,
						id:'feedercode',
						emptyText: '请采集飞达编号',
						allowBlank: false
					}]
				},{
					xtype: 'dataview',
					region : 'east',
					width: 300,
					id: 't_result',
					store: new Ext.data.Store({
						fields: ['type', 'text']
					}),
					cls: 'msg-body',
				    tpl: new Ext.XTemplate(
				    	'<audio id="audio-success" src="' + basePath + 'resource/audio/success.wav"></audio>',
				    	'<audio id="audio-error" src="' + basePath + 'resource/audio/error.wav"></audio>',
				    	'<tpl for=".">',
				            '<div class="msg-item">',
				            	'<tpl if="type == \'success\'"><span class="text-info">{text}</span></tpl>',
				            	'<tpl if="type == \'error\'"><span class="text-warning">{text}</span></tpl>',
				            '</div>',
				        '</tpl>'
				    ),
				    itemSelector: 'div.msg-item',
				    emptyText: '提示信息',
				    deferEmptyText: false,
				    autoScroll: true,
				    append: function(text, type) {
				    	type = type || 'success';
				    	this.getStore().add({text: text, type: type});
				    	this.getEl().scroll("b", this.getEl().getHeight(), true);  
				    	var el = Ext.get('audio-' + type).dom;
				    	el.play();
				    }
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'button',
					id : 'confirm',
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-gray',
					style: {
			    		marginLeft: '10px'
			        },
					width: 80
				},{
					xtype: 'button',
					id : 'returnAll',
					text: $I18N.common.button.erpReturnAllButton,
					cls: 'x-btn-gray',
					width: 80,
					style: {
			    		marginLeft: '10px'
			        }
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});