/**
 * 商机分配
 */	
Ext.define('erp.view.core.button.BusDistribute',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBusDistributeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'busdistributebtn',
    	text: $I18N.common.button.erpBusDistributeButton,
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
			grid.multiselected=[];
	        var items = grid.selModel.getSelection();
	        var domanArr = new Array();
		    var bcd_id = new Array();
			var bcdIdArr = '';
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
	        		domanArr.push(item);
	        	}
	        });
			var records = grid.multiselected;
			if(records.length > 0){
				Ext.each(domanArr,function(record){
					bcd_id.push(record.data['bcd_id']);
				});
				if(bcd_id.length>0){
					bcdIdArr = bcd_id.join(",");
				}
				this.bcdIdArr = bcdIdArr;
 				//||records[0].data.bc_agency==''||records[0].data.bc_agency==null
				if(records[0].data.bc_agency==undefined){			
					var ids = new Array();
					Ext.each(records, function(record, index){
						ids.push(record.data['bc_id']);
					});
					this.ids=ids;					
					this.showWin(records,grid,btn,domanArr);	

				}else{
					var bc_agency=null;	 
					var i=0;		
	        		Ext.each(records, function(item, index){
	        			if(bc_agency==null){
	        				
	        				bc_agency=item.data.bc_agency;	
	        			}

	        			if(bc_agency!=item.data.bc_agency){
	        				i=1;
	        				showError("请选择同一供应商的商机!");
	        				return;
	        			}	
	
	        		});
					if(i==0)
						{
			        		
			        		var ids = new Array();;
							Ext.each(records, function(record, index){
								ids.push(record.data['bc_id']);
							});
							this.ids=ids;
							var busdistributebtn=Ext.getCmp("busdistributebtn");
							busdistributebtn.showWin(records,grid,btn);	
			        	}
				}
			} else {
				showError("请勾选需要的明细!");
			}
		},
		getData:function(grid){
			var f="nvl(em_class,' ')<>'离职'";
			Ext.Ajax.request({
	        	url : basePath + 'ma/update/getEmpdbfindData.action',
	        	method : 'post',
	        	params : {
	        		fields:'em_id,em_code,em_name,em_depart,em_defaultorname,em_position',
		   			condition: f,
		   			page: -1,
		   			pagesize: 0
		   		},
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
		getDatalimit:function(grid,bc_agency){
			
			var f="nvl(em_class,' ')<>'离职' and em_defaultorcode in(select or_code from hrorg where AGENTNAME='"+bc_agency+"')";
			Ext.Ajax.request({
	        	url : basePath + 'ma/update/getEmpdbfindData.action',
	        	method : 'post',
	        	params : {
	        		fields:'em_id,em_code,em_name,em_depart,em_defaultorname,em_position',
		   			condition: f,
		   			page: -1,
		   			pagesize: 0
		   		},
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
		showWin:function(records,grid,btn,domanArr){
			var me=this;
			//var win = btn.win;
			var win=false;						
			if (!win) {
				win = Ext.create('Ext.Window', {
					id : 'emp-win',
					width : 600,
					height : 400,
					title : '员工',
					modal : true,
					//closeAction:'hide',
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
							dataIndex : 'em_id',
							hidden : true
						}, {
							text : '编号',
							dataIndex : 'em_code',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'em_code'}
						}, {
							text : '姓名',
							dataIndex : 'em_name',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'em_name'}
						}, {
							text : '部门',
							dataIndex : 'em_depart',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'em_depart'}
						},{
							text : '组织',
							dataIndex : 'em_defaultorname',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'em_defaultorname'}
						}, {
							text : '职位',
							dataIndex : 'em_position',
							flex : 1,
							filter: {xtype: 'textfield', filterName: 'em_position'}
						} ],
					store:Ext.create('Ext.data.Store',{
						fields : [ {
							name : 'em_id',
							type : 'number'
						}, 'em_code', 'em_name','em_depart','em_defaultorname', 'em_position' ],
						data:[],
						autoLoad:false
					}),
					listeners: {
						afterrender: function() {
							
							if(records[0].data.bc_agency==undefined ||records[0].data.bc_agency==''||
								records[0].data.bc_agency==null){
									me.getData(this);
								}else{
									me.getDatalimit(this,records[0].data.bc_agency);
								}
						}
					}
					}],
					buttonAlign: 'center',
					buttons: [{
						text: $I18N.common.button.erpConfirmButton,
						iconCls: 'x-btn-confirm',
						handler: function(btn) {
							me.confirm(grid,btn.ownerCt.ownerCt.down('gridpanel'));
							btn.ownerCt.ownerCt.close();
						}
					},{
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-btn-close',
						handler: function(btn) {
							
							btn.ownerCt.ownerCt.close();
						}
					}]
				});
			}
			btn.win = win;
			win.show();
		},
		
		confirm: function(grid,gl) {
			var ids=this.ids.join(",");
			var bcdids = this.bcdIdArr;
			var em_code=gl.selModel.lastSelected.get('em_code');
			var em_name=gl.selModel.lastSelected.get('em_name');			
			grid.setLoading(true);
			Ext.Ajax.request({
		   		url : basePath + 'crm/chance/busDistribute.action',
		   		params: {
		   			ids: ids,
		   			em_code:em_code,
		   			em_name:em_name,
		   			caller:caller,
		   			bcdids:bcdids
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