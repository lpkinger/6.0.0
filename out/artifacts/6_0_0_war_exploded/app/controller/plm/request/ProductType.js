Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.ProductType', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'erp.view.plm.base.ProductTypeTree'
    	],
    init:function(){
    	var me = this;
    	me.lastSelected = null;
    	var isDel = false;
    	this.control({ 
    		'erpProductTypeTreePanel': {
    			itemmousedown: function(view,record,el,index, e,eOpts){
    				me.lastSelected = record;
					me.loadTab(view, record);
    				var tree=Ext.getCmp('producttype');	
    				
    			},
    			itemdblclick: function(view, record){
    				me.lastSelected = record;
    				var btn = Ext.getCmp('confirm');
    				btn.fireEvent('click', btn);
    			}
    		},
    		'button[name=confirm]':{   
    			click:function(){
    				var data = parent.Ext.getCmp('grid').getSelectionModel().store.data.items;//假设项目阶段没有数据，不出现提示框
    				if(data&&data.length>0){
    					var checkData = new Array();
    					for(var i =0;i<data.length;i++){
    						var phase = data[i].data['pp_phase'];
    						if(''!=phase){
    							checkData[i] = phase;
    						}
    					}
    					//考虑到选择同一个产品类型是否覆盖
    					/*var oldproducttype = parent.Ext.getCmp('prj_producttype').value;
    					var newproducttype = me.lastSelected.data.text;*/
    					if(checkData.length>0){
    						/*if(oldproducttype==newproducttype){
    							me.afterclick(isDel);
        					}else{*/
        						Ext.MessageBox.confirm('提示', '将清空项目计划阶段，是否删除!', function(button,text){
        							if(button=='yes'){
        								isDel = true;
        								me.afterclick(isDel);
        							}else{
        								me.afterclick(isDel);
        							}
        						});
        					/*}*/
    					}else{
    						me.afterclick(isDel);
    					}
    	        	}else{
    	        		me.afterclick(isDel);
    	        	}
    			},
    			
    		},
    		'button[name=close]': {
    			click: function(){
    				parent.Ext.getCmp('win').close();
    			}
    		}
    	});
    },
    afterclick:function(isDel){
    	var me = this;
		var f = parent.Ext.ComponentQuery.query('producttypetrigger');
		var p = parent.Ext.getCmp('prj_producttypecode');
		if(f){
			if(me.lastSelected != null){
				if(me.lastSelected.childNodes.length==0){
					parent.Ext.getCmp('win').close();			
					f[0].setValue(me.lastSelected.data.text);
					if(p){
						p.setValue(me.lastSelected.data.data);
					}
				}
			}
		}
		
		//获取阶段计划数据
		var grid = parent.Ext.getCmp('grid');
		var record = grid.getSelectionModel();//获取所有数据
		var id = record.store.data.items[0].data['pp_prjid'];
        if(isDel){
        	Ext.Ajax.request({
        		url:basePath + "common/deleteDetail.action",
        		params : {
        			gridcaller: grid.caller,
        			condition: grid.mainField + "=" + id
        		},
        		asycn:false,
        		method : 'post',
        		callback : function(opt, s, res) {
        		}
        	});
        	grid.store.removeAll();
        	if(record.store.data.items.length<=0){
        		me.GridUtil.add10EmptyItems(grid, 10, true);
        	}
        }
		var productType = me.lastSelected.data.data;
		if(grid.store.getCount()==0){
			Ext.Ajax.request({
				url:basePath + 'plm/request/getProjectPhase.action',
				method:'post',
				params:{
					productType:productType
				},
				callback:function(options,success,response){
					var res = Ext.decode(response.responseText);
					if(res.success){
						if(res.data.length>0){
							grid.store.loadData(res.data);
						}									
					}
				}
			});
		}
	},
    loadTab: function(selModel, record){
    	var me = this;
    	var tree = Ext.getCmp('ProductTypeTree');
    	if (record.get('leaf')) {
    		return;
    	} else {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'plm/base/getRootProductType.action',
			        	params: {
			        		parentid: record.data['id']
			        	},
			        	async: false,
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
				}
			}
    	}
    }
});