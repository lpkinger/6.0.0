Ext.QuickTips.init();
Ext.define('erp.controller.pm.outsource.MakeMaterialGive', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'pm.outsource.MakeMaterialGive', 'core.grid.Panel5', 'common.editorColumn.GridPanel', 'core.grid.YnColumn',
      		'core.button.CreateDetail', 'core.button.PrintDetail', 'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
  	],
	init:function(){
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
	    me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'button[id=create]': {
				click: function(btn){
					warnMsg("确定要生成补料单吗?", function(btn){
    					if(btn == 'yes'){
    						var grid = Ext.getCmp('editorColumnGridPanel');
    						me.turnAdd(grid); 
    					}
    				});
				}
			},
			'button[name=query]': {
				click: function(btn){ 
					me.onQuery();
				}
			},
			'checkbox[id=whcode]' : {
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Give', 'GroupWarehouse.OS', function(bool) {
						f.setValue(bool);
                    });
                    me.BaseUtil.getSetting('MakeMaterial!OS!Give', 'showUserFactoryWh', function(v) {//物料分仓库存只显示登录用户所属工厂对应仓库库存信息
						var grid = Ext.getCmp('editorColumnGridPanel');
						grid.ifOnlyShowUserFactoryWh = v||false;					
					});
				}
			},
			'dbfindtrigger[name = mm_whcode]':{
				aftertrigger:function(f){
					var newvalue = f.value;
					var mm_id = f.record.data.mm_id;
					var isrep = f.record.data.isrep;
					var mpdetno = f.record.data.mm_detno;
					me.BaseUtil.getSetting('MakeMaterial!OS!Give', 'changeWhCode', function(v) {
						if(v){
							Ext.Ajax.request({
								url: basePath + 'pm/make/changeWhcode.action',
								params: {
									isrep: isrep,
									whcode: newvalue,
									mmid: mm_id,
									mpdetno:mpdetno
								},
								method: 'post',
								callback: function(options, success, response) {
									var localJson = new Ext.decode(response.responseText);
									if (localJson.exceptionInfo) {
										showError(localJson.exceptionInfo);
									}	   													
								}
							});
						}
					});
				
				}
			},
			'combo[id=groupPurs]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Give', 'isGroupPurc', function(v) {
						if(v){
							f.show();							
						}
					});
				}
			},
			'dbfindtrigger[name=ma_code]':{
				aftertrigger:function(){
					var record = Ext.getCmp('grid').selModel.getLastSelected(); 
					record.set('ma_thisqty',0);
				}
			},
			'erpEditorColumnGridPanel':{
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Give', 'Select!OS!issue', function(bool) {
						isSelect = bool;
                    });
				},
				selectionchange:function(selectionModel, selected, options){
					if(!isSelect){
						Ext.each(selected,function(item){
							var sum=0;
							var ma_code = item.data['mm_code'];
							var mm_detno = item.data['mm_id'];
							Ext.each(selected,function(s){
								if(ma_code==s.data['mm_code'] && mm_detno==s.data['mm_id']){
									sum+=s.data['mm_thisqty'];
								}
							});
							Ext.each(selected,function(a){
								if(ma_code==a.data['mm_code'] && mm_detno==a.data['mm_id']){
									a.set('mm_total', sum);
								}
							});
						});
					}
				},
				deselect:function(row,record,index,eOpts){
					record.set('mm_total',0);
				},
				edit:function(e,o,eOpts){
//					if(o.originalValue<o.value){
//						return;
//					}
					var grid = Ext.getCmp('editorColumnGridPanel');
					grid.fireEvent('selectionchange',grid.selModel.selectionMode, grid.selModel.selected.items, '');
				}
			},
			'combo[id=filterByPrKind]':{//按照物料大类筛选
				beforerender: function(f) {
					var me=this;
					me.BaseUtil.getSetting('MakeMaterial!OS!Give', 'filterByPrKind', function(v) {
						if(v){
							f.show();	
						}
					});
				},
				change: function(field,n,o) {				
					me.onQuery();				
				}
			}
		});
	},
	onQuery: function(){
		var me = this,grid = Ext.getCmp('grid');
		//计算thisqty
		this.calAddQty(grid);
		var condition = null;
		Ext.each(grid.store.data.items, function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				if (item.data['ma_id']==null || item.data['ma_id']==''){
					showError('制造单号'+item.data['ma_code']+'必须从放大镜选择');   
				}else{
					if(condition == null){
						condition = "(mm_code='" + item.data['ma_code'] + "'";
					} else {
						condition += " OR mm_code='" + item.data['ma_code'] + "'";
					}
				} 
			}
		});		
		if(condition == null){
			condition = " 1=2 ";//未录入有效工单，则不筛选任何数据
		}else{
			condition +=  ")";
		}
		if (Ext.getCmp('pr_location')){
			var location=Ext.getCmp('pr_location'); 
			if (location && location.value!=''){ 
				if (Ext.getCmp('ifnulllocation').checked){
					condition+="and (pr_location like '%"+location.value+"%' or NVL(pr_location,' ')=' ')";
				}else{
					condition+="and pr_location like '%"+location.value+"%' ";
				}
			}			
		}
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value != ''){
				condition += " and "+ grouppurs.value ;
			}
		}
		var filterByPrKind = Ext.getCmp('filterByPrKind');//显示剩余需要领料数为0的物料
		if(filterByPrKind && !filterByPrKind.hidden){
			var va = filterByPrKind.value;
			if(va!='' &&  va!=null && va!='全部'){
			   condition +=" and pr_kind='"+va+"' ";
			}
		}
		if(condition != null){
			grid.multiselected = new Array();
			grid.busy = true;
			var dg = Ext.getCmp('editorColumnGridPanel');
			dg.selModel.deselectAll(true);
			dg.busy = true;
			condition += " AND ( nvl(mm_scrapqty,0)+nvl(mm_returnmqty,0)-nvl(mm_balance,0)-nvl(mm_addqty,0)-NVL(mm_turnaddqty,0)>0)";
			//修改成这种方式可以减少render 时间，以前的方式先加载主料render,在逐条插入替代料，
			//每插入一次替代料就所有数据render一次，到时render浪费很多时间
			dg.reloadData(condition + ' order by mm_maid,mm_detno', function(gridData){
				me.showReplace(condition, function(repData){
					dg.store.loadData(me.mergeRepData(gridData, repData));
					dg.store.fireEvent('load', dg.store);
					dg.fireEvent('storeloaded', dg);
				});
			});
			setTimeout(function(){
				dg.busy = false;
				grid.busy = false;
			}, 1000);
		}
	
	},
	/**
	 * 计算可补料数
	 **/
	calAddQty: function(grid){
		var items = grid.store.data.items, idx = new Array();
		Ext.each(items, function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				idx.push(item.data['ma_id']);
			}
		});
		if(idx.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'pm/make/calAddQty.action',
				async: false,
				params: {
					ids: Ext.Array.concate(idx, ',')
				},
				callback: function(opt, s, r){
					var res = Ext.decode(r.responseText);
					if(res.exceptionInfo) {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	/**
	 * 替代料
	 */
	showReplace: function(condition,callback){
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join make on mm_maid=ma_id left join Product on mp_prodcode=pr_code' + 
	   				' left join productwh on pw_whcode=mp_whcode and pw_prodcode=mp_prodcode',
	   			fields: 'mp_assignqty as mm_assignqty,mp_mmid,mp_detno,mm_thisqty as mp_thisqty,mp_canuseqty,mp_repqty,mp_haverepqty,mm_turnaddqty,mp_remark,mp_prodcode,pr_detail,pr_spec,pr_unit,pr_location,mp_whcode,ma_vendcode,pw_onhand,pr_whmancode,pr_kind,pr_whmanname',
	   			condition: condition + ' and ( nvl(mm_scrapqty,0)+nvl(mm_returnmqty,0)-nvl(mm_balance,0)-nvl(mm_addqty,0)-NVL(mm_turnaddqty,0)>0)'
	   		},
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}else if(localJson.success){
    				data = Ext.decode(localJson.data);
    			}
	   			callback && callback.call(null, data);    	
	   		}
		});
	},
	turnAdd: function(grid) {
		var me = this,
			material = me.getEffectData(grid.selModel.getSelection());
		if(material.length > 0){
			grid.setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'pm/make/turnAdd.action',
		   		params: {
		   			data: Ext.encode(material),
		   			wh: Ext.getCmp('whcode').checked,
		   			caller: caller,
		   			type: 'OS'
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			grid.setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
		   			if(localJson.log){
		   				showMessage('提示', localJson.log);
		   			}
	    			if(localJson.success){
	    				turnSuccess(function(){
	    					grid.multiselected = new Array();
	    				});
		   			}
	    			me.onQuery();
		   		}
			});
		}
	},
	getEffectData: function(items) {
		var d = new Array();
		Ext.Array.each(items, function(item){
			if (item.get('mm_thisqty') != 0) {
				d.push({
					mm_detno: item.get('mm_detno'),
					mm_code: item.get('mm_code'),
					mm_id: item.get('isrep') == null ? item.get('mm_id') : -item.get('mm_id'),
					mm_thisqty: item.get('mm_thisqty'),
					mm_whcode: item.get('mm_whcode'),
					ma_vendcode:item.get('ma_vendcode'),
					ma_apvendcode: item.get('ma_apvendcode') == null?"":item.get('ma_apvendcode'),
					pr_whmancode:item.get('pr_whmancode'),
					pr_kind:item.get('pr_kind')
				});
			}
		});
		return d;
	},
	/**
	 * 合并补料数据、替代料数据
	 */
	mergeRepData: function(gridData, repData) {
		var me = this, datas = [];
		Ext.Array.each(gridData, function(d, i){
			datas.push(d);
			Ext.Array.forEach(repData, function(r){
				if(d.mm_id == r.MP_MMID) {
					datas.push(me.parseRepData(d, r));
				}
			});
		});
		return datas;
	},
	/**
	 * replace data 替代料数据 转成普通格式的grid data
	 */
	parseRepData: function(gridItem, repItem) {
		return {
			mm_prodcode: repItem.MP_PRODCODE,
			mm_oneuseqty: gridItem.mm_oneuseqty,
			mm_code: gridItem.mm_code,
			pr_detail: repItem.PR_DETAIL,
			pr_spec: repItem.PR_SPEC,
			pr_unit: repItem.PR_UNIT,
			mm_canuserepqty: repItem.MP_CANUSEQTY,
			mm_thisqty: repItem.MP_THISQTY,
			mm_totaluseqty: repItem.MP_REPQTY,
			mm_havegetqty: repItem.MP_HAVEREPQTY,
			mm_qty: repItem.MP_CANUSEQTY,
			mm_turnaddqty: repItem.MM_TURNADDQTY,
			mm_ifrep: 1,
			mm_remark: repItem.MP_REMARK,
			mm_whcode: repItem.MP_WHCODE,
			mm_detno: repItem.MP_DETNO,
			mm_id: repItem.MP_MMID,
			isrep: true,
			pr_location:repItem.PR_LOCATION,
			ma_vendcode: repItem.MA_VENDCODE,
			ma_apvendcode:gridItem.ma_apvendcode,
			pw_onhand:repItem.PW_ONHAND,
			pr_whmancode:repItem.PR_WHMANCODE,
			pr_kind:repItem.PR_KIND,
			mm_assignqty:repItem.MM_ASSIGNQTY,
			pr_whmanname:repItem.PR_WHMANNAME
		};
	}
});