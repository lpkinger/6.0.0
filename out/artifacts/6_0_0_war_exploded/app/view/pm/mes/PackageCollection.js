Ext.define('erp.view.pm.mes.PackageCollection',{ 
	extend: 'Ext.Viewport', 
    layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id:'form',
				title:'包装采集',
				xtype: 'form',
				anchor: '100% 30%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				autoScroll: true,
				scrollable: true,
				items:[{
					xtype: 'fieldcontainer',					
					defaults: {
						width: 250,
						msgTarget:'side'
					}, 
					layout: 'column',
					items: [{
							xtype: 'dbfindtrigger',
							fieldLabel: '资源编号',
							allowBlank:false,
							id:'sc_code',
							name:'sc_code',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;"
						},{
							xtype: 'textfield',
							fieldLabel: '资源名称',
							readOnly:true,						
							id:'sc_name',
							name:'sc_name'
						},{
							xtype: 'textfield',
							fieldLabel: '工序编号',
							readOnly:true,
							id:'st_code',
							name:'st_code'
						},{
							xtype: 'textfield',
							fieldLabel: '工序名称',
							readOnly:true,
							id:'st_name',
							name:'st_name'
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '作业单号',
							id:'mc_code' ,
							name:'mc_code',
							allowBlank:false,
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red;"
						},{
							xtype: 'textfield',
							fieldLabel: '制造单号',
							id:'mc_makecode' ,
							name:'mc_makecode',
							allowBlank:false,
							hidden : true,
							hideLabel:true
						},{			
						    xtype: 'textfield',
							fieldLabel: '产品编号',
							readOnly:true,
							id:'mc_prodcode',
							name:'mc_prodcode'
						},{
							xtype: 'textfield',
							fieldLabel: '产品名称',
							readOnly:true,
							id:'pr_detail',
							name:'pr_detail'
						},{
							xtype: 'textfield',
							fieldLabel: '产品ID',
							id:'pr_id',
							name:'pr_id',
							hidden : true,
							hideLabel : true
						},{
							xtype: 'textfield',
							fieldLabel: '数量',
							readOnly:true,
							id:'mc_qty',
							name:'mc_qty'
						},{
						    xtype: 'boxcodetrigger',
							fieldLabel: '箱号',
							allowBlank:false,
							id:'pa_code',
							name:'pa_code',
							fieldStyle : "background:rgb(224, 224, 255);",    
				            labelStyle:"color:red"
						},{
							xtype: 'textfield',
							fieldLabel: '箱内容量',
							allowBlank:false,							
							id:'pa_totalqty',
							name:'pa_totalqty',							
				            readOnly : true
						},{
						    xtype: 'textfield',
							fieldLabel: '已装数量',
							readOnly : true,
							id:'pa_inqty'
						},{
						    xtype: 'textfield',
							fieldLabel: '剩余可装数',
							readOnly : true,
							id:'pa_restqty'
				  }]			 
			    }],
			    buttonAlign: 'center',
				buttons: [{
					xtype: 'erpQueryButton'						
				  },{
					xtype: 'erpCloseButton'					
				},{
					id:'updateQty',
					text:'更新箱内容量'
				}]
			},{			   
				xtype: 'grid',
				anchor: '100% 45%',
				id:'querygrid',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				columns: [{
					text: '箱号',
					dataIndex: 'pd_outboxcode',
					flex: 1
				},{
					text: '序列号',
					dataIndex: 'pd_barcode',
					flex: 1					
				},{
					text: '数量',
					dataIndex: 'pd_innerqty',
					flex: 1					
				},{
					text : '操作',
					cls : 'x-grid-header-1',
					flex: 1,
					xtype: 'buttoncolumn',
					buttons: [{
						text: '清除采集结果',
						handler: function(view, cell, recordIndex, cellIndex, e) {
							var record = view.getStore().getAt(recordIndex);
							me.clear(record);
						}						
					}]				
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: ['pd_outboxcode','pd_barcode','pd_innerqty'],			  
			        data: [],
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
				anchor: '100% 10%',
				bodyStyle: 'background: #f1f1f1;',
				bodyPadding:5,
				items: [{
					xtype: 'fieldcontainer',
					autoScroll: true,
					scrollable: true,
					/*defaults: {
						width: 250
					},*/
					/*layout: {
						type: 'table',
						columns: 4
					},*/
					layout:'column',
					
					items: [{
						xtype: 'textfield',
						fieldLabel: '序列号',
						id:'ms_sncode',
						columnWidth: 0.23,
						labelWidth:60,
						//colspan: 1,
						allowBlank: false,
						fieldStyle : "background:rgb(224, 224, 255);",    
				        labelStyle:"color:red;"	
					},{
						xtype: 'textfield',
						fieldLabel: '序列号开头',
						id:'ms_codeB',
						columnWidth: 0.20
					},{
						xtype: 'textfield',
						fieldLabel: '序列号长度',
						id:'ms_codeLength',
						columnWidth: 0.20
					},/*{
						xtype: 'combo',
						fieldLabel: '标签模板',
						id: 'template',
						columnWidth: 0.24,	
						//autoSelect:true,
						queryMode: 'remote',
						store: Ext.create('Ext.data.Store', {
							autoLoad: true,
						    fields: ['lps_code','lps_id'],
						    proxy: {
					             type: 'ajax',
							     url : basePath + 'pm/mes/getTemplates.action',
							     extraParams:{condition:'package'},
							     reader: {
							          type: 'json',
							          root: 'datas'
							     },
							     headers: {
					                 'Content-Type': 'application/json;charset=utf-8'
					             }		                   
					           },
					          listeners:{
					          	load : function (store){
					          		Ext.getCmp('template').select(store.getAt(0));								
					          	}								
					           }
						}),
					    displayField: 'lps_code',
					    valueField: 'lps_id',					
					    allowBlank:false														
					},*/{
						xtype: 'erpPrintButton'	,
						width:'80px'
					}]
				}]	
			 }] 
		}); 
		me.callParent(arguments); 
	} ,
	 clear:function(record){
    	var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'pm/mes/clearPackageDetail.action',
	   		params: {
	   			caller   : caller,
	   			outbox   : record.data.pd_outboxcode,
	   			sncode	 : record.data.pd_barcode
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
    			if(r.success){
    				Ext.getCmp("querygrid").store.remove(record);
    				Ext.getCmp('pa_inqty').setValue(Ext.getCmp("querygrid").store.data.length);
    				Ext.getCmp('t_result').append('清除采集结果：'+record.data.pd_barcode+'成功！');
    				showMessage('提示', '清除采集结果成功!', 1000);
	   			}
	   		}
		});  
    }
});