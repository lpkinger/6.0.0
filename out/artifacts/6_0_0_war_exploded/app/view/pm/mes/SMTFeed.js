Ext.define('erp.view.pm.mes.SMTFeed',{ 
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
				xtype: 'erpQueryGridPanel',
				anchor: '100% 55%'
			},{
				xtype: 'form',
				anchor: '100% 25%',
				id:'sform',
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
					layout: {
						type: 'table',
						columns: 2
					},
					items: [{
						xtype: 'textfield',
						fieldLabel: '备料单号',
						readOnly: false,
						emptyText: '可锁定备料单号以防止上错料卷',
						id:'mpcode',
						colspan: 2,
						width: 600,
						allowBlank: false
					},{
						xtype: 'fieldcontainer',
						fieldLabel : '操作', 
						defaultType: 'radiofield',
						layout: 'hbox',
						colspan: 2,
						width: 500,
						defaults: {
			                flex: 1
			            },
			            items: [ {
			                boxLabel  : '上料',
			                inputValue: 'get',
			                name: 'operator',
			                id        : 'get',
			                checked: true
			            },{
			            	boxLabel  : '退料',
			                inputValue: 'back',
			                name: 'operator',
			                id        : 'back'
			            }, {
			                boxLabel  : '换料卷',
			                inputValue: 'change',
			                name: 'operator',
			                id        : 'change'
			            },{
			                boxLabel  : '接料',
			                inputValue: 'add',
			                name: 'operator',
			                id        : 'add'
			            },]
					},{
						xtype: 'textfield',
						fieldLabel: '站位',
						readOnly:true,
						colspan: 1,
						id:'mlscode',
						plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
					},{
						xtype: 'textfield',
						fieldLabel: '料卷编号',
						readOnly:true,
						colspan: 1,
						id:'barcode',
						plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
					},{
						xtype: 'textfield',
						fieldLabel: '飞达编号',
						readOnly:true,
						colspan: 1,
						id:'fecode',
						plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
					},{
						xtype: 'textfield',
						fieldLabel: '录入框',
						readOnly:false,
						colspan: 1,
						id:'input',
						allowBlank: true,
						emptyText:'请录入站位编号'					
					},{
						xtype: 'textfield',
						fieldLabel: '卷料数量',
						readOnly:true,
						colspan: 1,
						id:'number'
					},{
						xtype: 'combo',
						fieldLabel: '版面',
						colspan: 1,
						id:'tableAB',
						store: Ext.create('Ext.data.Store', {
						   fields: ['display', 'value'],
						   data : [{"display": 'A', "value": 'A'},
						           {"display": 'B', "value": 'B'}]
					   }),
					   displayField: 'display',
					   valueField: 'value',
					   queryMode: 'local'
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
					width: 60
				},{
					xtype: 'button',
					id : 'blankAll',
					text: $I18N.common.button.erpBlankAllButton,
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