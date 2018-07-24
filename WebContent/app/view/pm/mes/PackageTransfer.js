Ext.define('erp.view.pm.mes.PackageTransfer',{ 
	extend: 'Ext.Viewport', 
    layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'包装转移',
				xtype: 'form',
				anchor: '100% 25%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				autoScroll: true,
				scrollable: true,
				items:[{
					xtype: 'fieldcontainer',					
					defaults: {
						width: 250
					},     
					layout: {
						type: 'table',
						columns: 5
					},
					items: [{
							xtype: 'textfield',
							fieldLabel: '箱号',
							colspan: 1,
							allowBlank:false,
							id:'pa_outboxcode',
							name:'pa_outboxcode',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
				            emptyText:'请录入需要转移的箱号'
						},{
							xtype: 'textfield',
							fieldLabel: '箱内数量',
							readOnly:true,						
							colspan: 1,
							id:'pa_totalqty',
							name:'pa_totalqty'
						},{
							xtype: 'boxcodetrigger2',
							fieldLabel: '目标箱号',
							colspan: 1,
							id:'pa_outboxnew',
							name:'pa_outboxnew',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;",
							emptyText:'请录入目标箱号'
						},{
							xtype: 'textfield',
							fieldLabel: '箱内数量',
							colspan: 1,
							readOnly:true,	
							id:'pa_totalqtynew' ,
							name:'pa_totalqtynew',
							allowBlank:false,
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;"
						}]			 
			    }],
			    buttonAlign: 'center',
				buttons: [{
					xtype: 'erpCloseButton'					
				}]
			},{			   
				xtype: 'grid',
				anchor: '100% 40%',
				id:'querygrid',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				columns: [{
					text: '箱号',
					dataIndex: 'pd_outboxcode',
					flex: 1
				},{
					text: '序列号/子箱号',
					dataIndex: 'pd_barcode',
					flex: 1					
				},{
					text: '数量',
					dataIndex: 'pd_innerqty',
					flex: 1					
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: ['pd_outboxcode','pd_barcode','pd_innerqty'],			  
			        data: [ {},{},{},{},{},{},{},{},{},{},{}],
                    autoLoad:true
			     })			
			},{
				xtype: 'dataview',
				anchor: '100% 15%',
				id: 't_result',
				autoScroll: true,
				scrollable: true,
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
		    },{				
				xtype: 'form',
				anchor: '100% 20%',
				bodyStyle: 'background: #f1f1f1;',
				items: [{
					xtype: 'fieldcontainer',
					autoScroll: true,
					scrollable: true,
					defaults: {
						width: 250
					},
					layout: {
						type: 'table',
						columns: 4
					},
					items: [{						
						xtype: 'fieldcontainer',
						fieldLabel : '操作', 
						defaultType: 'radiofield',
						layout: 'hbox',
						colspan: 4,
						width: 500,
						defaults: {
			                flex: 1
			            },
			            items: [ {
			                boxLabel  : '序列号',
			                inputValue: 'serial',
			                name: 'operator',
			                id        : 'serial',
			                checked: true
			            },{
			            	boxLabel  : '子箱号',
			                inputValue: 'package',
			                name: 'operator',
			                id        : 'package'
			            }]					
					},{
						xtype: 'textfield',
						fieldLabel: '录入框',
						id:'entercode',
						colspan: 1,
						allowBlank: false,
						fieldStyle : "background:rgb(224, 224, 255);",    
				        labelStyle:"color:red;"	
					},{
						xtype: 'combo',
						fieldLabel: '打印选择',
						id:'choose',
						colspan: 1,
						store: Ext.create('Ext.data.Store', {
						   fields: ['display', 'value'],
						   data : [{"display": '新箱号', "value": '0'},
						           {"display": '旧箱号', "value": '1'},
						           {"display": '新旧箱号', "value": '2'}]
					   }),
					   displayField: 'display',
					   valueField: 'value',
					   queryMode: 'local'
					},{
						xtype: 'erpPrintButton'	,
						width:'80px'
					}]
				}]	
			 }] 
		}); 
		me.callParent(arguments); 
	} 
});