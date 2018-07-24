Ext.define('erp.view.core.button.ComboButton',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpComboButton',
	cls: 'x-btn-gray',
	text: '下拉框设置',
	hidden: false,
	disabled:true,
	style: {
		marginLeft: '10px'
	},
	initComponent : function(){
		this.callParent(arguments); 
	},
	comboSet:function(caller, field){
		var me=this;
		var combodata=me.getComboData(caller,field);
		Ext.create('Ext.Window', {
			width: '80%',
			height: '80%',
			autoShow: true,
			layout: 'border',
			buttonAlign : 'center',
			title:'<h1>下拉配置</h1>',
			items: [{
				region:'center',
				xtype: 'grid',
				id:'combogrid',
				columnLines:true,
				bodyStyle: 'background-color:#f1f1f1;',
				plugins: Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}),
				listeners:{
					itemclick:function(selModel, record,e,index){			    
						var grid=selModel.ownerCt;
						Ext.getCmp('deletecombo').setDisabled(false);
						if(index.toString() == 'NaN'){
							index = '';
						}
						if(index == grid.store.data.items.length-1){//如果选择了最后一行
							var items=grid.store.data.items;
							var detno=Math.ceil(items[index].data.dlc_detno);
							for(var i=0;i<10;i++){
								detno++;
								var o = new Object();
								o.dlc_caller=caller;
								o.dlc_fieldname=field;
								o.dlc_value=null;
								o.dlc_value_en=null;
								o.dlc_value_tw=null;
								o.dlc_display=null;
								o.dlc_detno=detno;
								grid.store.insert(items.length, o);
								items[items.length-1]['index'] = items.length-1;
							}
						}
					}	
				},
				columns: [{
					text:'ID',
					dataIndex:'dlc_id',
					width:0
				},{
					cls : "x-grid-header-1",
					text:'序号',
					dataIndex: 'dlc_detno',
					xtype: 'numbercolumn',
					flex: 0.5,
					format: '0,000',
					editor: {
						format:'0',
						xtype: 'numberfield',
						hideTrigger:true,
						minValue:0
					},
					renderer: function(val, meta, record){
						if(!val){
							val=0;
						}
						return Math.ceil(val);						  
					}
				},{
					cls : "x-grid-header-1",
					text: '中文值',
					dataIndex: 'dlc_value',
					flex: 1,
					editor: {
						format:'',
						xtype: 'textfield'
					},
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
					}
				},{
					cls : "x-grid-header-1",
					text: '英文值',
					dataIndex: 'dlc_value_en',
					flex: 1,
					editor: {
						format:'',
						xtype: 'textfield'
					}
				},{
					cls : "x-grid-header-1",
					text:'繁文',
					dataIndex: 'dlc_value_tw',
					flex: 1,
					editor: {
						format:'',
						xtype: 'textfield'
					}
				},{
					cls : "x-grid-header-1",
					text:'实际值',
					dataIndex:'dlc_display',
					flex:1,
					editor: {
						format:'',
						xtype: 'textfield'
					},			
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
					}
				},{
					cls : "x-grid-header-1",
					text:'Caller',
					dataIndex: 'dlc_caller',
					flex:1,
					readOnly:true
				},{
					cls : "x-grid-header-1",
					text:'字段名',
					dataIndex:'dlc_fieldname',
					flex:1,
					readOnly:true
				}]
			}],
			bbar:['->',{
				xtype:'button',
				text: $I18N.common.button.erpSaveButton,
				iconCls: 'x-button-icon-save',
				cls: 'x-btn-gray',
				formBind: true,//form.isValid() == false时,按钮disabled
				width: 60,
				style: {
					marginLeft: '10px'
				},
				handler:function(){
					me.saveCombo(caller,field);
				}
			},{
				xtype:'button',
				text: $I18N.common.button.erpDeleteButton,
				iconCls: 'x-button-icon-delete',
				cls: 'x-btn-gray',
				id:'deletecombo',
				width: 60,
				style: {
					marginLeft: '10px'
				},
				handler:function(){
					me.deleteCombo(me);
				}

			},{
				xtype:'button',
				text: $I18N.common.button.erpCloseButton,
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				formBind: true,//form.isValid() == false时,按钮disabled
				width: 60,
				style: {
					marginLeft: '10px'
				},
				handler:function(btn){      	
					btn.ownerCt.ownerCt.close();

				} 
			},'->']
		});
		Ext.getCmp('combogrid').getStore().loadData(combodata);
	},
	getComboData:function(caller,field){
		var combodata=null;
		Ext.Ajax.request({
			url : basePath +'common/getComboDataByCallerAndField.action',
			params: {
				caller:caller,
				field:field
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);

					return;
				}
				if(res.success){
					combodata=res.data;
				}

			} 

		});
		if(combodata.length<1){
			this.add10EmptyData(combodata,caller, field);
		}
		return combodata;
	},
	saveCombo:function(caller,field){
		var me=this;
		var grid=Ext.getCmp('combogrid');
		grid.necessaryField='dlc_value';
		var jsonGridData = new Array();
		var dd;
		var s = grid.getStore().data.items;
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			dd = new Object();
			if(data[grid.necessaryField] != null && data[grid.necessaryField] != ""){
				Ext.each(grid.columns, function(c){
				dd[c.dataIndex] = s[i].data[c.dataIndex];
				});
				jsonGridData.push(Ext.JSON.encode(dd));
			}
		}
		var param=jsonGridData;
		if(param == null || param == ''){
			showError($I18N.common.grid.emptyDetail);
		}else {
			var params = new Object();
			params.gridStore = unescape(param.toString().replace(/\\/g,"%"));
			Ext.Ajax.request({
		   		url : basePath + 'common/saveCombo.action',
		   		params : params,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var res=new Ext.decode(response.responseText);
		   			if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}
	                if(res.success){
	                 Ext.Msg.alert('提示','保存成功!',function(){
	                	 grid.getStore().loadData(me.getComboData(caller,field));
	                 });				                 
	                }
		   		}
			});
		}
	},
	deleteCombo:function(){
	  var grid= Ext.getCmp('combogrid');
	  var id= grid.getSelectionModel().lastSelected.data['dlc_id'];
	  if(id!=null&&id!="null"){
		  //存在数据后台删除
		  Ext.Ajax.request({
		   		url : basePath + 'common/deleteCombo.action',
		   		params : {
		   			id:id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var res=new Ext.decode(response.responseText);
		   			if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}
	                if(res.success){
	                 Ext.Msg.alert('提示','删除成功!');
	                 grid.getStore().remove(grid.getSelectionModel().getSelection());
	                }
		   		}
			});
	  }else {
		  //不存在数据直接remove
		 grid.getStore().remove(grid.getSelectionModel().getSelection());
	  }
	  Ext.getCmp('deletecombo').setDisabled(true);
	},
	add10EmptyData: function(data,caller, field){
			for(var i=0;i<10;i++){
				var o = new Object();
				o.dlc_caller=caller;
				o.dlc_fieldname=field;
				o.dlc_value=null;
				o.dlc_value_en=null;
				o.dlc_value_tw=null;
				o.dlc_display=null;
				o.dlc_detno=i;
				data.push(o);
			}
	}
});