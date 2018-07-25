Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMBatchExpand', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.BOMBatchExpand','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan','core.button.Export',
    		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.button.ExportArrayBom','core.button.CleanDetail','core.button.BOMExpand',
  			'core.button.BOMExpandAll'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			//bom展开
			'erpBOMExpandButton' : {
				click: function(btn){
					me.beforeBomExpand();
				}
			},
			//清除明细
			'erpCleanDetailButton' : {
				click: function(btn){
					var param = me.getGridItems();
					if(param == null || param == ''){
						showError("没有需要清除的明细");
						return ;
					}	
					me.cleanDetail();
				}
			},
			//bom全部展开
			'erpBOMExpandAllButton' : {
				click: function(btn){
					var param = me.getGridItems();
					if(param == null || param == ''){
						showMessage('提示', '没有需要展开的明细!', 1000);
						return;
					}		
					var mb = new Ext.window.MessageBox();
				     mb.wait('正在展开','请耐心等待完成...',{
					   interval: 10000,
					   duration: 1000000,
					   increment: 20, 
					   scope: this
					});
					Ext.Ajax.request({
				   		url : basePath+'pm/bom/bomStructAll.action',
				   		params: {
				   			emid:Ext.getCmp('em_id').value,
				   			caller:caller
				   		},
				   		method : 'post',
			        	timeout: 600000,
				   		callback : function(options,success,response){
				   			mb.close();
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				showMessage('提示', '展开成功!', 1000); 
				   			} else if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo; 
			        			showError(str);return;
			        		} 
				   		}
					});
				}
			},
			//导出矩阵bom
			'erpExportArrayBomButton' : {
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var param = me.getGridItems();
					if(param == null || param == ''){
						showError("请先选择需要导出矩阵的BOM");
						return ;
					}
					var gridStore = unescape(param.toString());
					me.FormUtil.setLoading(true);//loading...	
					Ext.Ajax.request({
				   		url : basePath+'pm/bom/bomExpand.action',
				   		params: {
				   			id:Ext.getCmp('em_id').value,
				   			gridStore:gridStore
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){ 
				   			me.FormUtil.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				showMessage('提示', '操作成功!', 1000);
			    				window.location.href=basePath+"excel/exportBatchBOMAsExcel.action?caller="+caller+"&_noc=1";
			    				grid.GridUtil.loadNewStore(grid, {
					               caller: caller,
					               condition: gridCondition
					             });
				   			} else if(localJson.exceptionInfo){ 
			        			showError(localJson.exceptionInfo);return;
			        		}
				   		}
					});		
				}
			},
			/*'erpExportButton' : {
				 afterrender:function(btn){
					 btn.handler=function(){
						 var con="bs_topmothercode in   (select bb_prodcode  from BOMBatch where bb_emid="+em_uu+") ";
						 me.BaseUtil.createExcel('BOMStruct!Struct!Query', 'detailgrid', con);
					 };	
				 }
			 },*/
			 'erpPrintButton': { 
	    			click:function(btn){
					var reportName="BomLevel2";
					var condition="{bombatch.bb_emid}="+Ext.getCmp('em_id').value+" and {bomstruct.bs_sonbomid}>0  and {bom.bo_mothercode}<>{bomstruct.bs_soncode}  and  {BomStruct.bs_supplytype}<>'VIRTUAL' and  ({product_1.pr_dhzc}<>'MRP' or isnull({product_1.pr_dhzc}))";
					var id=Ext.getCmp('em_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);
				}
	    	}
		});
	},
	cleanDetail: function(){
		var grid=Ext.getCmp('grid'); 
		grid.setLoading(true);
		 Ext.Ajax.request({
	   		url :basePath+ 'pm/bom/cleanBOMBathExpand.action',
	   		params: {
	   			id:Ext.getCmp('em_id').value
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){ 
	   			grid.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '操作成功!', 1000);
    				//update成功后刷新页面进入可编辑的页面
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){ 
        			showError(localJson.exceptionInfo);return;
        		}
	   		}
		});		
	},
	beforeBomExpand: function(){
		//form里面数据
		var me = this;		
		var grid = Ext.getCmp('grid');
		var param = me.getGridItems();
		if(param == null || param == ''){
			showError("请先选择需要批量展开的BOM");
			return ;
		}
		var gridStore = unescape(param.toString());
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath+'pm/bom/bomExpand.action',
	   		params: {
	   			id:Ext.getCmp('em_id').value,
	   			gridStore:gridStore
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '操作成功!', 1000); 
    				 var con="bs_topmothercode in   (select bb_prodcode  from BOMBatch where bb_emid="+em_uu+") ";
					 me.BaseUtil.createExcel('BOMStruct!Struct!Query', 'detailgrid', con);
    				 grid.GridUtil.loadNewStore(grid, {
                            caller: caller,
                            condition: gridCondition
                     });   				
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo; 
        			showError(str);return;
        		} 
	   		}
		});	
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getGridItems :function(){//检查明细行是否有明细
		var	grid = Ext.getCmp('grid');  
		var jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			grid.getStore().each(function(item){//将grid里面各行的数据获取并拼成jsonGridData
				var data = Ext.clone(item.data);
				var dd = new Object();
				if(data[grid.necessaryField] != null && data[grid.necessaryField] != ""){
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					Ext.each(grid.columns, function(c){
						if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
								} else {
									dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d');//如果用户没输入日期，或输入有误，就给个默认日期，
									//或干脆return；并且提示一下用户
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + data[c.dataIndex];
								}
							}else{
								dd[c.dataIndex] = data[c.dataIndex];
							}
					   }
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			});
		   return jsonGridData;
	   }
    }
});