Ext.define('erp.view.pm.mes.CraftMaterial',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%'
			},{
				xtype: 'grid',
				anchor: '100% 48%',
				id:'qgrid',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				columns: [{
					text: '物料编号',
					dataIndex: 'sp_soncode',
					flex: 1,
					renderer:function(val, meta, record){
						meta.style = "background: #e0e0e0;";
						return val;
					}
				},{
					text: '物料名称',
					dataIndex: 'pr_detail',
					flex: 1	,
					renderer:function(val, meta, record){
						meta.style = "background: #e0e0e0;";
						return val;
					}
				},{
					text: '物料规格',
					dataIndex: 'pr_spec',
					flex: 1	,
					renderer:function(val, meta, record){
						meta.style = "background: #e0e0e0;";
						return val;
					}
				},{
				    text: '物料ID',
					dataIndex: 'pr_id',					
					flex: 1,
					hidden:true
				},{
				    text: '条码前缀',
					dataIndex: 'pr_prefix',					
					flex: 1,
					editor :{
						xtype:'textfield'
					}
				},{
				    text: '条码长度',
					dataIndex: 'pr_length',					
					flex: 1,
					editor :{
						xtype:'numberfield',
						minValue: 1,
						maxValue: 30
					}
				},{
				    text: '替代料编号',
					dataIndex: 'sp_repcode',
					flex: 1	,
					renderer:function(val, meta, record){
						meta.style = "background: #e0e0e0;";
						return val;
					}
				},{
				    text: '是否采集',
					dataIndex: 'if_pick',
					flex: 1	,
					renderer:function(val, meta, record){
						meta.style = "background: #e0e0e0;";
						if(val == '未采集'){
						   return '<span style="color:red;">' + val + '</span>';
						}										
		                else return val;
					}
				},{
				    text: 'id',
					dataIndex: 'sp_id',
					flex: 1	,
					hidden:true
				},{
				    text: '采集类型',
					dataIndex: 'sp_type',
					flex: 1	,
					hidden:true
				}],
				 plugins: [
			        Ext.create('Ext.grid.plugin.CellEditing', {
			            clicksToEdit: 1
			        })
			    ],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: ['sp_soncode','pr_detail','pr_spec','pr_id','pr_prefix','sp_repcode','if_pick'],			  
			        data: [ {},{},{},{},{},{},{},{},{},{},{}],
                    autoLoad:true
			     })
			},{
				xtype: 'form',
				anchor: '100% 22%',
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
						xtype: 'fieldcontainer',
						fieldLabel : '操作', 
						defaultType: 'radiofield',
						layout: 'hbox',
						colspan: 2,
						width: 450,
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
			            	boxLabel  : '取消上料',
			                inputValue: 'back',
			                name: 'operator',
			                id        : 'back'
			            }]
					},{
						xtype: 'dbfindtrigger',
						fieldLabel: '序列号',
						readOnly:true,
						colspan: 2,
						id:'ms_code',
						plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
					},{
						xtype: 'textfield',
						fieldLabel: '录入框',
						readOnly:false,
						colspan: 2,
						id:'input',
						allowBlank: true
					}]
				},{
					xtype: 'dataview',
					region : 'east',
					width: 350,
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
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});