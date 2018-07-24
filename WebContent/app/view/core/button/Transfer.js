/**
 * 商机转移
 */	
Ext.define('erp.view.core.button.Transfer',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTransferButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'transferbtn',
    	text: $I18N.common.button.erpTransferButton,
    	style: {
    		marginLeft: '10px'
        },
        width: null,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn) {
			var me=this;
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		var bool = true;
	        		Ext.each(grid.multiselected, function(){
	        			if(this.data[grid.keyField] == item.data[grid.keyField]){
	        				bool = false;
	        			}
	        		});
	        		if(bool){
	        			grid.multiselected.push(item);
	        		}
	        	}
	        });
			var records = grid.multiselected;
			if(records.length > 0){
				var ids = new Array();
				Ext.each(records, function(record, index){
					ids.push(record.data['bc_id']);
				});
				this.ids=ids;
				this.showWin(grid,btn);	
			} else {
				showError("请勾选需要的明细!");
			}
		},
		getData:function(grid){
			Ext.Ajax.request({
	        	url : basePath + 'common/getFieldsDatas.action',
	        	method : 'post',
	        	params : {
	        		fields:'bd_id,bd_name,bd_prop,bd_admincode,bd_admin,bd_overtime,bd_remark,bd_agency',
	        		condition: "1=1",
		   			caller:'BUSINESSDATABASE'
		   		},
			    method : 'post',
			    callback : function(opt, s, res){
			       var r = new Ext.decode(res.responseText);
			       if(r.exceptionInfo){
			    		showError(r.exceptionInfo);return;
			    	} else if(r.success && r.data){
			    	var data = Ext.decode(r.data.replace(/,}/g, '}').replace(/,]/g, ']'));
			    	grid.getStore().loadData(data);		    
			    	}
			    }
			});
		},
		showWin:function(grid,btn){
			var me=this;
			var win = btn.win;
			if (!win) {
				win = Ext.create('Ext.Window', {
					id : 'bus-win',
					width : 800,
					height : 500,
					title : '商机库',
					modal : true,
					closeAction:'hide',
					layout: 'anchor',
					items : [ {
						xtype : 'gridpanel',
						anchor: '100% 100%',
						autoScroller:true,
						columnLines : true,
						plugins : [Ext.create(
								'erp.view.core.grid.HeaderFilter'											
						), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						singleSelect:true,
						columns : [ {
							text : 'ID',
							dataIndex : 'BD_ID',
							hidden : true
						}, {
							text : '名称',
							dataIndex : 'BD_NAME',
							flex : 1.5,
							filter: {xtype: 'textfield', filterName: 'BD_NAME'}
						}, {
							text : '属性',
							dataIndex : 'BD_PROP',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'BD_PROP'}
						},{
							text : '管理员编号',
							dataIndex : 'BD_ADMINCODE',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'BD_ADMINCODE'}
						}, {
							text : '管理员名称',
							dataIndex : 'BD_ADMIN',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'BD_ADMIN'}
						}, {
							text : '所属代理商',
							dataIndex : 'BD_AGENCY',
							flex : 1.5,
							filter: {xtype: 'textfield', filterName: 'BD_AGENCY'}
						} ],
					store:Ext.create('Ext.data.Store',{
						fields : [ {
							name : 'BD_ID',
							type : 'number'
						}, 'BD_NAME', 'BD_PROP','BD_ADMINCODE','BD_ADMIN','BD_AGENCY' ],
						data:[],
						autoLoad: false
					}),
					listeners: {
						afterrender: function() {
							me.getData(this);
						}
					}
					}],
					buttonAlign: 'center',
					buttons: [{
						text: $I18N.common.button.erpConfirmButton,
						iconCls: 'x-btn-confirm',
						handler: function(btn) {
							me.confirm(grid,btn.ownerCt.ownerCt.down('gridpanel'));
							btn.ownerCt.ownerCt.hide();
						}
					},{
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-btn-close',
						handler: function(btn) {
							btn.ownerCt.ownerCt.hide();
						}
					}]
				});
			}
			btn.win = win;
			win.show();
		},
		
		confirm: function(grid,gl) {
			var ids=this.ids.join(",");
			var bd_name=gl.selModel.lastSelected.get('BD_NAME');
			grid.setLoading(true);
			Ext.Ajax.request({
		   		url : basePath + 'crm/chance/transfer.action',
		   		params: {
		   			ids: ids,
		   			bd_name:bd_name,
		   			caller:caller
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			grid.setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			} else {
		   				if(localJson.success){
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery(true);
		   				}
		   			}
		   		}
			});
	  }
	});