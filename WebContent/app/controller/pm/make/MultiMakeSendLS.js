Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MultiMakeSendLS', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'pm.make.MultiMakeSendLS','pm.make.MakeSendLSGrid','common.batchDeal.Form',
            'core.form.ConDateField','core.button.Close','core.form.YnField','core.form.MultiField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
    		,'core.grid.YnColumn','core.trigger.AddDbfindTrigger'
    	],
    init:function(){ 
    	var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
		me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=query]': {
				click: function(btn){
					var me = this;
					if(Ext.getCmp('ma_code').value ==null||Ext.getCmp('ma_code').value ==''){
						showError('制造单号为空,请填写以后再筛选!');
						return;
					}
					if(Ext.getCmp('ma_xbwhouse').value ==null||Ext.getCmp('ma_xbwhouse').value ==''){
						showError('线边仓库必须填写!');
						return;
					}
					me.getId(Ext.getCmp('ma_code').value);             
				}
			},
			'button[id=add]': {
				click: function(btn){
					if(Ext.getCmp('ma_departmentcode').value ==null||Ext.getCmp('ma_departmentcode').value ==''){
						showError('部门编号为空, 请重新填写!');
						return;
					}
					if(Ext.getCmp('ms_recordorcode').value ==null||Ext.getCmp('ms_recordorcode').value ==''){
						showError('人员编号为空, 请重新填写!');
						return;
					}

					var grid = Ext.getCmp('batchDealGridPanel');
					warnMsg("确定要发料吗?", function(btn){
    					if(btn == 'yes'){
    						me.create(grid);
    					}
    				});
				
				}
			}
    	});
    },
    getProductWh: function(grid) {
		var codes = [];
		grid.store.each(function(d){
			codes.push("'" + d.get('mm_prodcode') + "'");
		});
		if (codes!=[]){
			Ext.Ajax.request({
				url: basePath + 'scm/product/getProductwh.action',
				params: {
					codes: codes.join(','),
					caller:caller
				},
				callback: function (opt, s, r) {
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.data) {
							grid.productwh = rs.data;
						}
					}
				}
			});
		}
	},
    onQuery: function(result){
		var grid = Ext.getCmp('grid');
		var me=this;
		this.setQty(result);
		if(!grid){
			grid = Ext.getCmp('batchDealGridPanel');
		}
		grid.multiselected = new Array();
		var form = Ext.getCmp('dealform');
		var cond = form.getCondition();
		if(Ext.isEmpty(cond)) {
			cond = '1=1';
		}
		var pr_whmanname = Ext.getCmp('pr_whmanname');
		if(pr_whmanname && pr_whmanname.value !=null && pr_whmanname.value !=''){
			var conds=cond.split("pr_whmanname");
			if(Ext.getCmp('ma_blank').checked==true){
				cond = conds[0]+'pr_whmanname is null or pr_whmanname'+conds[1];
			}
		}
		var pr_location = Ext.getCmp('pr_location');
		if(pr_location && pr_location.value !=null && pr_location.value !=''){
			var conds=cond.split("pr_location");
			if(Ext.getCmp('ifnulllocation').checked==true){
				cond = conds[0]+'pr_location is null or pr_location'+conds[1];
			}
		}
		var mm_thisqtygtzero = Ext.getCmp('mm_thisqtygtzero');
		if(mm_thisqtygtzero && mm_thisqtygtzero.checked==true){
			cond+=" and (nvl(mm_canuseqty,0)-nvl(mm_haverepqty,0)-nvl(mm_repqty,0)>0 or nvl(mm_thisqty,0)>0)"; 
		}
		cond+=" and nvl(mm_materialstatus,' ')=' '"; 
		var pushType=false; //判断是否考虑推式物料
		this.BaseUtil.getSetting(caller, 'pushSupplyType', function(v) {
			pushType=v; 
			if (pushType!=true){ 
				cond+=" and pr_supplytype='PULL'"; 
			}
			form.beforeQuery(caller, cond);//执行查询前逻辑 
			grid.getGridColumnsAndStore(grid, 'common/singleGridPanel.action',{caller: caller, condition: cond, start: 1, end: 1000}, "",true);
			var condition = "1=1";
			if(Ext.getCmp('ma_id').value != null&&Ext.getCmp('ma_id').value !=''){
				condition="( mp_maid in ("+Ext.getCmp('ma_id').value+"))";
			}
			condition+=" and nvl(mm_materialstatus,' ')=' '";
			if (pushType!=true){ 
				condition+=" and pr_supplytype='PULL'"; 
			}
			me.showReplace(condition, grid);
		});  
		
	},
	showReplace: function(condition, grid){
		var me = this;
		var mm_thisqtygtzero = Ext.getCmp('mm_thisqtygtzero');
		if(mm_thisqtygtzero && mm_thisqtygtzero.checked==true){
			condition +=" and nvl(mp_thisqty,0)>0"; 
		}
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join Product on mp_prodcode=pr_code left join productwh on pw_prodcode=mp_prodcode and mp_whcode=pw_whcode' ,
	   			fields: 'mp_mmid,mp_detno,mp_thisqty,mp_canuseqty,mp_repqty,mp_prodcode,mp_whcode,pr_detail,pr_spec,'+
	   				'pr_unit,mp_haverepqty,mp_thisplanqty,mp_wipuseqty,mp_canuseqty,mm_level,mm_oneuseqty,pr_wiponhand,pr_location,'+
	   				'pr_supplytype,pw_onhand,mm_maid,mp_mmdetno,mm_prodcode',
	   			condition: condition + ' order by mp_mmid,mp_detno'
	   		},
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
    			if(localJson.success){
    				var data = Ext.decode(localJson.data);
    				if(data && data.length > 0) {
    					var idx=0, store = grid.store;
    					grid.lockrender = true;
    					Ext.each(data, function(d){ 
    						Ext.Array.each(store.data.items,function(item,index){   							
    							if(item.data.mm_id==d.MP_MMID){
    								idx=index;
    								return;
    							}
    						});
    						record = store.getAt(idx);
    						if(idx != null  && idx >= 0) {
    							store.insert(idx + 1, {
    								mm_prodcode:d.MP_PRODCODE,
    								mm_oneuseqty: d.MM_ONEUSEQTY,
    								mm_qty: d.MP_CANUSEQTY,
    								pr_detail: d.PR_DETAIL,
    								pr_spec: d.PR_SPEC,
    								pr_unit: d.PR_UNIT,
    								mm_thisqty: d.MP_THISQTY,
    								mm_totaluseqty: d.MP_REPQTY,
    								mm_whcode: d.MP_WHCODE,
    								mm_detno: d.MP_DETNO,
    								mm_id: d.MP_MMID,
    								havegetqty: d.MP_HAVEREPQTY,
    								mm_level:d.MM_LEVEL,
    								mm_thisplanqty:d.MP_THISPLANQTY,
    								mm_wipuseqty:d.MP_WIPUSEQTY,
    								pr_wiponhand:d.PR_WIPONHAND,
    								isrep: true,
    								pr_location:d.PR_LOCATION,
    								pr_supplytype:d.PR_SUPPLYTYPE,
    								pw_onhand:d.PW_ONHAND,
    								mm_maid:d.MM_MAID,
    								mm_remark:"序号:"+d.MP_MMDETNO+",主料:"+d.MM_PRODCODE
    							});
    						}
    					});
    					grid.lockrender = false;
    				}
    				grid.store.fireEvent('load', grid.store);
    				me.getProductWh(grid);
	   			}
	   		}
		});
	},
	setQty: function(){
		var ma_id = Ext.getCmp('ma_id').value;
		if(ma_id.indexOf('#')>0){
			ma_id = ma_id.replace(/\#/g,',');
		}
		var wipwhcode = Ext.getCmp('ma_xbwhouse').value;
		Ext.Ajax.request({
			url : basePath + 'pm/make/setLSThisQty.action',
			async: false,
			params: {
				caller:caller,
				ma_id:ma_id,
				qty:0,
				wipwhcode:wipwhcode
			},
			callback: function(opt, s, r){
				var res = Ext.decode(r.responseText);
				if(res.exceptionInfo) {
					showError(res.exceptionInfo);
				}
			}
		});
	},
	create:function(grid){ 
		var me = this, 
			material = this.getEffectData(grid.selModel.getSelection());
		var wipwhcode=Ext.getCmp('ma_xbwhouse').value; 
		var pi_departmentcode="",pi_emcode="",pi_cgycode="";
		if (Ext.getCmp('ma_departmentcode')){
			pi_departmentcode=Ext.getCmp('ma_departmentcode').value;
		};
		if (Ext.getCmp('ms_recordorcode')){
			pi_emcode=Ext.getCmp('ms_recordorcode').value;
		};
		if (Ext.getCmp('pr_whmancode')){
			pi_cgycode=Ext.getCmp('pr_whmancode').value;
		};
		if(material.length > 0){
			grid.setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'pm/make/multiturnlssend.action',
		   		params: {
		   			data: Ext.encode(material),
		   			bywhcode: Ext.getCmp('bywhcode').checked,
		   			wipwhcode: wipwhcode,
		   			maid:Ext.getCmp('ma_id').value,
		   			caller:caller,
		   			departmentcode:pi_departmentcode,
		   			emcode:pi_emcode,
		   			cgycode:pi_cgycode
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
	    					
	    				});
		   			}
	    			me.onQuery();
		   		}
			});
		}
	
	},
	getEffectData: function(items) {
		var d = new Array();
		var code = Ext.getCmp('ma_code').value;
		Ext.Array.each(items, function(item){
			if(item.get('mm_thisqty') > 0 || item.get('mm_thisplanqty') > 0) {
				d.push({
					mm_maid: item.get('mm_maid'),
					mm_id: item.get('isrep') == null ? item.get('mm_id') : -item.get('mm_id'),
					mm_detno: item.get('mm_detno'),		
					mm_thisqty: item.get('mm_thisqty'),
					mm_thisplanqty: item.get('mm_thisplanqty'),
					mm_wipuseqty: item.get('mm_wipuseqty'),
					mm_whcode: item.get('mm_whcode'),
					mm_prodcode: item.get('mm_prodcode') 
				});
			}
		});
		return d;
	},
	getId : function(code){
		var des = '';
		var me = this;
		code = code.replace(/\#/g,'\',\'');
		Ext.Ajax.request({
	   		url : basePath + 'pm/bom/getDescription.action',
	   		params: {
	   		tablename: "make",
    		field: "wm_concat(ma_id)",
			condition: "ma_code in ('"+code+"') and ma_statuscode='AUDITED'"
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
		   		if(res.exceptionInfo){
		   			showError(res.exceptionInfo);return;
	    		}
	    		if(res.success && res.description != null){
	    			des = res.description;
	    			if(code.split(',').length > des.split(',').length){
	    				showError("输入的单号中有不存在的制造单!");
	    				return;
	    			}
	    			Ext.getCmp("ma_id").setValue(des);
	    			me.onQuery();			
    		    }else {
	    		   	showError("制造单编号非法或者未审核！");
	    		   	var store = Ext.getCmp("batchDealGridPanel").store;
	   				//清空所有 		   				
	   				store.removeAll();
    		    }
	   		}
		});
	}
});