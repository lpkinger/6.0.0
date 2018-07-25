/**
 * 
 */
Ext.define('erp.view.scm.product.GetUUid.ComponentGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpComponentGrid',
	requires: ['erp.view.core.grid.ButtonColumn'],
	layout : 'fit',
	id: 'uuIdGrid', 
 	emptyText : '无数据',
    columnLines : true,
	id:'uuIdGrid',
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpComponentGridToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],	
	columnLines: true,
	store: Ext.create('Ext.data.Store',{
		fields: ['brand','uuid','code','spec','unit','weight','img','action'],			  
        data: [],
        autoLoad:true
     }),
	columns: [{
		text: '图片',
		dataIndex: 'img',
		cls: 'x-grid-header-1',
		width: 80,
		renderer: function(val, meta, record) {				
			if(val){
				var thumb = val.substr(0, val.lastIndexOf(".")) + "_60x60" + val.substr(val.lastIndexOf("."));
		         return '<div class="x-grid-cell-image"><img src="'+thumb+'" width="60" height="60" /></div>';
			 } else{
			 	 meta.tdCls = 'x-text-muted';
				 return "暂无图片"
			 }		
		}
	},{
		text: '品牌',
		dataIndex: 'brand',
		flex: 1,
		titleAlign:'center',
		cls: 'x-grid-header-1',
		renderer: function(val, meta, record) {
			if(val.nameCn != null) {
				return val.nameCn;
			}else{
			    return "";
			}
		}
	},{
		text: '标准料号',
		dataIndex: 'uuid',
		flex: 1,
		cls: 'x-grid-header-1',
		hidden:true
	},{
		text: '原厂型号',
		dataIndex: 'code',
		flex: 1.5,	
		cls: 'x-grid-header-1',
		renderer: function(val, meta, record) {
			meta.tdCls = 'x-text-link';
		    meta.tdAttr = 'data-qtip="点击查看详情"';		 
			return val ;
		}
	},{
		text: '规格',
		dataIndex: 'spec',
		cls: 'x-grid-header-1',
		flex: 1					
	},{
	    text: '单位',
		dataIndex: 'unit',
		cls: 'x-grid-header-1',
		width: 60
	},{
	    text: '净重',
		dataIndex: 'weight',
		cls: 'x-grid-header-1',
		width: 60
	},{
		text: '操作',
		dataIndex: 'action',
		cls: 'x-grid-header-1',
		xtype: 'buttoncolumn',
		width: 60,
		buttonText: '选择',
		handler: function(view, cell, recordIndex, cellIndex, e) {
			var record = view.store.getAt(recordIndex), da = record.data;
			var cal;
			if(parent.caller == 'ProductBatchUUId' || caller=='ProductBatchUUId'){//需要修改将数据自动写回至后台
				if(caller == 'ProductBatchUUId' ){
					var grid = Ext.getCmp('grid');
				}else{
					var grid = parent.Ext.getCmp('grid');
				}
				var lastRe = grid.getSelectionModel().getLastSelected();
				cal= 'ProductBatchUUId';
				//提示与原有的原厂型号不一致
				var oldcode = lastRe.data["pr_orispeccode"];
				var param = lastRe.data;
				param.pub_uuid = da.uuid;
				param.pub_orispeccode = da.code;
				param.unit = da.unit;
				if(oldcode&&oldcode!=''&&oldcode != da.code){
					warnMsg("当前选择的原厂型号与原有的原厂型号不一致，是否确认更改", function(btn){
						if(btn == 'yes'){
							Ext.getCmp('uuIdGrid').confirmChoose(param,cal);									
						} else {
							return;
						}
					});
				}else{
					 Ext.getCmp('uuIdGrid').confirmChoose(param,cal);	
				}
			}else if(parent.caller == 'MRPOnhandThrow'){
				var grid = parent.Ext.getCmp('batchDealGridPanel');
				var lastRe = grid.getSelectionModel().getLastSelected();
				var param = lastRe.data;
				param.uuid = da.uuid;
				param.orispeccode = da.code;
				param.unit = da.unit;
			    cal = 'MRPOnhandThrow';
				Ext.getCmp('uuIdGrid').confirmChoose(param,cal);
				parent.Ext.getCmp('uuWin').close();
			}else if(parent.Ext.getCmp('form')){
				if(parent.Ext.getCmp('pre_uuid')){
					parent.Ext.getCmp('pre_uuid').setValue(da.uuid);
					parent.Ext.getCmp('pre_brand').setValue(da.brand.nameCn);
				    parent.Ext.getCmp('pre_orispeccode').setValue(da.code);
				}else if(parent.Ext.getCmp('pr_uuid')){
					parent.Ext.getCmp('pr_uuid').setValue(da.uuid);
					parent.Ext.getCmp('pr_brand').setValue(da.brand.nameCn);
				    parent.Ext.getCmp('pr_orispeccode').setValue(da.code);
				}
				parent.Ext.getCmp('uuWin').close();
			}		
		}
	}],			
	initComponent : function(){ 
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		viewready: function() {
			pageSize = Math.ceil(this.view.el.getHeight() / 66);
		},
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		cellclick:function(view, td, colIdx, record, tr, rowIdx, e){
			var field = view.ownerCt.columns[colIdx].dataIndex;
			var da = record.data;
			if (field == 'code') {
				Ext.Ajax.request({//拿到B2C 地址，通过后台获取是生产还是测试环境
		        	url : basePath + 'common/getB2CUrl.action',
		        	async: false,
		        	callback : function(options,success,response){
		        		var res = new Ext.decode(response.responseText);
		        	    if(res.exceptionInfo){
			   				showError(res.exceptionInfo);
		        	    }else{
		        	    	window.open(res.b2curl+'/product#/component/'+da.uuid+'/');
		        	    }
		        	}
		        });
			}else if(field == 'action'){/*
				if(Ext.getCmp('wind')){
					if(caller == 'ProductBatchUUId'){
						var grid = parent.Ext.getCmp('grid');
						//最后一次选中的行
						var lastRe = grid.getSelectionModel().getLastSelected();	
						lastRe.set("pub_uuid",da.uuid);
					}if(Ext.getCmp('form')){
						if(Ext.getCmp('pre_uuid')){
							Ext.getCmp('pre_uuid').setValue(da.uuid);
						    Ext.getCmp('pre_orispeccode').setValue(da.code);
						}else if(Ext.getCmp('pr_uuid')){
							Ext.getCmp('pr_uuid').setValue(da.uuid);
						    Ext.getCmp('pr_orispeccode').setValue(da.code);
						}
					}
					Ext.getCmp('wind').close();
				}
			*/}
		}
	},
	getGridData:function(kindId,page,pageSize,search){
    	var tree = Ext.getCmp('tree-panel');
    	var param = {
    		kindId: kindId,
	        page:page,
	        pageSize:pageSize
    	};
    	if(search != null && search != '' ){
    		param.orispeccode = search;
    	}
    	 tree.setLoading(true, tree.body);
    	 Ext.Ajax.request({//拿到tree数据
	        	url : basePath + 'scm/product/getProductComponent.action',
	        	params:param,
	        	async: false,
	        	callback : function(options,success,response){
	        		tree.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.gridStore !=null ){
		        		Ext.getCmp('uuIdGrid').store.loadData(res.gridStore.content);
		        		//获取总条数
		        		dataCount = res.gridStore.totalElements;
		        		//总页数
		        		Ext.getCmp('pagingtoolbar').afterOnLoad();
	        		}else{
	        			Ext.getCmp('uuIdGrid').store.loadData('');
		        		//获取总条数
		        		dataCount = 0;
		        		//总页数
		        		Ext.getCmp('pagingtoolbar').afterOnLoad();
	        		}
	        	}
	        });
    },
    confirmChoose:function(param,cal){   	
    	var grid = Ext.getCmp('uuIdGrid');
    	 grid.setLoading(true);
    	 Ext.Ajax.request({//确认选择
	        	url : basePath + 'scm/product/confirmUUId.action',
	        	params:{param:unescape(escape(Ext.JSON.encode(param))),caller:cal},
	        	async: false,
	        	callback : function(options,success,response){
	        		grid.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.success){
	        			if(cal == 'MRPOnhandThrow'){
	        			   parent.Ext.getCmp('batchDealGridPanel').getSelectionModel().getLastSelected();
	        			   parent.Ext.getCmp('uuWin').close();
	        			    /*Ext.each(items,function(item,idx){							
								if(item.data['sp_id'] == r.result.replace('success', '')){								   	
									index = idx;
									sp_id = item.data['sp_id'];
									pr_code = item.data['sp_soncode'];
									this.set('if_pick','未采集');
									this.commit();
								}
						    });*/
	        			}else{
	        				if(caller == 'ProductBatchUUId'){
	        				    var g = Ext.getCmp('grid');
	        				    g.getData(g.BaseUtil.getUrlParam('gridCondition').replace(/IS/g, "="));
	        				    Ext.getCmp("wind").close();
	        				}else{
	        					var g = parent.Ext.getCmp('grid');
	        					g.getData(g.BaseUtil.getUrlParam('gridCondition').replace(/IS/g, "="));
	        					parent.Ext.getCmp('uuWin').close();
	        				}
	        			}
	        		} else if(res.exceptionInfo){
		   				showError(res.exceptionInfo);
	        		}
	        	}
	        });  
    }
});