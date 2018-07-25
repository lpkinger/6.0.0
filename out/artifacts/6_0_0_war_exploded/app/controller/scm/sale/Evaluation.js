Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.Evaluation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.Evaluation','core.grid.Panel2','scm.sale.EvaluationProcess','scm.sale.EvaluationProduct','core.toolbar.Toolbar',
      		'core.form.MultiField','core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
      		'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.grid.YnColumn','core.button.BOMCost','core.button.BOMInsert',
      		'core.button.BOMVastCost','core.button.TurnOffPrice','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
      		'core.form.FileField','core.button.BOMOfferCost','core.grid.Panel5','core.button.CleanDetail','core.trigger.MultiDbfindTrigger','core.button.EditDetail'
		],
        init:function(){
        	var me = this;
    		this.control({
    			'erpGridPanel2': {
        			itemclick: this.onGridItemClick
        		},
        		'EvaluationProduct': { 
        			itemclick: this.onGridItemClick
        		},
        		'EvaluationProcess': { 
        			itemclick: this.onGridItemClick
        		},
        		'field[name=ev_currency]': {
	    			beforetrigger: function(field) {
	    				var t = field.up('form').down('field[name=ev_date]'),
	    					value = t.getValue();
	    				if(value) {
	    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
	    				}
	    			}
    			},
    			'field[name=ev_bomid]': {
        			beforerender: function(field){
    					if(Ext.getCmp('ev_code')&&Ext.getCmp('ev_code').value){
    						field.readOnly=true;
    					}
    				},
        			delay: 200
        		},
        		'field[name=ev_currency]': {
        			beforerender: function(field){
    					if(field.value){
    						field.readOnly=true;
    					}
    				},
        			delay: 200
        		},
        		'erpDeleteDetailButton': {
        			afterrender: function(btn){
        				btn.handler = function() {
        					var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
            				Ext.Ajax.request({//拿到form的items
            					url : basePath + "scm/sale/deleteEvaluationDetail.action",
    	    			        params:{
    	    			        	id: record.data['evd_id'],
    	    			        	caller: caller
    	    	   				},
    	    			        method : 'post',
    	    			        callback : function(options,success,response){  
    	    			        	var result=Ext.decode(response.responseText);
    	    			        	if(result.success){
    	    			        		Ext.Msg.alert('提示','删除成功!',function(){
    	    			        			window.location.reload();
    	    			        		});
    	    			        	}else{
    	    			        		if(result.exceptionInfo != null){
    	    			            		showError(result.exceptionInfo);return;
    	    			            	}
    	    			        	}
    	    			        }
    	    				});	  
        				};
	    			 }
	    		 },
        		'erpCleanDetailButton':{
        			beforerender: function(btn){
        				btn.setText('清除报价材料明细');
        				btn.setWidth(150);
        			},
        			click: function(btn){
        				var ev_id = Ext.getCmp('ev_id').value,
        				grid = Ext.getCmp('grid');
        				warnMsg("确认清除报价材料明细?", function(btn){
			    			if(btn == 'yes'){
		        				me.FormUtil.setLoading(true);
		            			Ext.Ajax.request({
		            				url: basePath + 'scm/sale/clearbomoffer.action',
		            				params: {
		            					ev_id: ev_id
		            				},
		            				timeout: 600000,
		            				callback: function(opt, s, r) {
		            					me.FormUtil.setLoading(false);
		            					var rs = Ext.decode(r.responseText);
		            					if(rs.success) {
		            						showMessage('清除报价材料明细成功');
		            						window.location.reload();
		            						//me.GridUtil.loadNewStore(grid, {caller: caller, condition: 'evd_evid=' + ev_id});
		            					} else if(r.exceptionInfo) {
		            						showError(r.exceptionInfo);
		            					}
		            				}
		            			});
				    		}
			    		});
        			}
        		},
    			'erpSaveButton': {
    				click: function(btn){  
    					var form = me.getForm(btn);
    					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    						me.BaseUtil.getRandomNumber();//自动添加编号
    					}
    					var ev_materialcost=Ext.getCmp('ev_materialcost').value,
    						ev_makecost=Ext.getCmp('ev_makecost').value,
    						ev_mancost=Ext.getCmp('ev_mancost').value,
    						ev_othercost=Ext.getCmp('ev_othercost').value,
    						statdate = Ext.getCmp('ev_fromdate').value,
        					enddate = Ext.getCmp('ev_validdate').value;
        				if(!Ext.isEmpty(enddate)){
        					if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
            					bool=false;
            					showError('失效日期不能小于生效日期!');return;
            				}
            				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
            					bool=false;
            					showError('失效日期不能小于当前日期!');return;
            				}
        				}
    					Ext.getCmp('ev_cost').setValue(Number(ev_materialcost)+Number(ev_makecost)+Number(ev_mancost)+Number(ev_othercost));
    					
    					me.beforeSave();
    				}
    			}
        		,
    			'erpDeleteButton' : {
    				click: function(btn){
    					me.FormUtil.onDelete(Ext.getCmp('ev_id').value);
    				}
    			},
    			'erpUpdateButton': {
    				afterrender: function(btn){
        				var status = Ext.getCmp('ev_checkstatuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
    				click: function(btn){
    					var ev_materialcost=Ext.getCmp('ev_materialcost').value,
    						ev_makecost=Ext.getCmp('ev_makecost').value,
    						ev_mancost=Ext.getCmp('ev_mancost').value,
    						ev_othercost=Ext.getCmp('ev_othercost').value,
    						statdate = Ext.getCmp('ev_fromdate').value,
        					enddate = Ext.getCmp('ev_validdate').value;
        				if(!Ext.isEmpty(enddate)){
        					if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
            					bool=false;
            					showError('失效日期不能小于生效日期!');return;
            				}
            				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
            					bool=false;
            					showError('失效日期不能小于当前日期!');return;
            				}
        				}
    					Ext.getCmp('ev_cost').setValue(Number(ev_materialcost)+Number(ev_makecost)+Number(ev_mancost)+Number(ev_othercost));
    					
    					me.beforeUpdate();
    				}
    			},
    			'erpAddButton': {
    				click: function(){
    					me.FormUtil.onAdd('addEvaluation', '新增估价单', 'jsps/scm/sale/evaluation.jsp');
    				}
    			},
    			'erpCloseButton': {
    				click: function(btn){
    					me.FormUtil.beforeClose(me);
    				}
    			},
    			'erpSubmitButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value != 'ENTERING'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					var statdate = Ext.getCmp('ev_fromdate').value,
	    					enddate = Ext.getCmp('ev_validdate').value;
	    				if(!Ext.isEmpty(enddate)){
	    					if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
	        					bool=false;
	        					showError('失效日期不能小于生效日期!');return;
	        				}
	        				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
	        					bool=false;
	        					showError('失效日期不能小于当前日期!');return;
	        				}
	    				}
    					me.onSubmit(Ext.getCmp('ev_id').value,true);
    				}
    			},
    			'erpResSubmitButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value != 'COMMITED'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onResSubmit(Ext.getCmp('ev_id').value);
    				}
    			},
    			'erpAuditButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value != 'COMMITED'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onAudit(Ext.getCmp('ev_id').value);
    				}
    			},
    			'erpResAuditButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value != 'AUDITED'){
    						btn.hide();
    					}
    				},
    				click: function(btn){
    					me.FormUtil.onResAudit(Ext.getCmp('ev_id').value);
    				}
    			},
    			'erpPrintButton': {
    				click:function(btn){
    				var reportName="sale_gj";
    				var condition='{evaluation.ev_id}='+Ext.getCmp('ev_id').value+'';
    				var id=Ext.getCmp('ev_id').value;
    				me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}

    			},
    			'erpBannedButton': {
    				afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value == 'BANNED'){
    						btn.hide();
    					}
    				},
        			click: function(btn){
        				this.FormUtil.onBanned(Ext.getCmp('ev_id').value);
        			}
        		},
        		'erpResBannedButton': {
        			afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value != 'BANNED'){
    						btn.hide();
    					}
    				},
        			click: function(btn){
        				this.FormUtil.onResBanned(Ext.getCmp('ev_id').value);
        			}
        		},
        		'erpBOMOfferCostButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ev_checkstatuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        					ev_id = Ext.getCmp('ev_id').value,
        					bo_id = Ext.getCmp('ev_offerbomid').value,
        					pr_code = Ext.getCmp('ev_offerprcode').value,
        					grid = Ext.getCmp('grid');
        				var datas = me.GridUtil.getAllGridStore(grid);
        				if(datas.length>0){
        					warnMsg("报价材料明细中已经有数据，计算将覆盖原有数据。确认计算?", function(btn){
			    				if(btn == 'yes'){
			    					if(bo_id&&!Ext.isEmpty(bo_id)){
				        				form.setLoading(true);
				            			Ext.Ajax.request({
				            				url: basePath + 'scm/sale/bomoffercost.action',
				            				params: {
				            					ev_id: ev_id,
				            					bo_id: bo_id,
				            					pr_code: pr_code
				            				},
				            				timeout: 600000,
				            				callback: function(opt, s, r) {
				            					form.setLoading(false);
				            					var rs = Ext.decode(r.responseText);
				            					if(rs.success) {
				            						alert('计算完成!');
				            						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'ev_id=' + ev_id});
				            						me.GridUtil.loadNewStore(grid, {caller: caller, condition: 'evd_evid=' + ev_id});
				            					} else if(r.exceptionInfo) {
				            						showError(r.exceptionInfo);
				            					}
				            				}
				            			});
			    					}else{
			    						showError('报价BOM为空，不能计算!');
			    					}
			    				}
	        				});
        				}else{
        					if(bo_id&&!Ext.isEmpty(bo_id)){
		        				form.setLoading(true);
		            			Ext.Ajax.request({
		            				url: basePath + 'scm/sale/bomoffercost.action',
		            				params: {
		            					ev_id: ev_id,
		            					bo_id: bo_id,
		            					pr_code: pr_code
		            				},
		            				timeout: 600000,
		            				callback: function(opt, s, r) {
		            					form.setLoading(false);
		            					var rs = Ext.decode(r.responseText);
		            					if(rs.success) {
		            						alert('计算完成!');
		            						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'ev_id=' + ev_id});
		            						me.GridUtil.loadNewStore(grid, {caller: caller, condition: 'evd_evid=' + ev_id});
		            					} else if(r.exceptionInfo) {
		            						showError(r.exceptionInfo);
		            					}
		            				}
		            			});
	    					}else{
	    						showError('报价BOM为空，不能计算!');
	    					}
        				}
        			}
        		},
        		'erpBOMCostButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ev_checkstatuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        				btn.setText('参考材料成本计算');
        				btn.setWidth(150);
        			},
        			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        					ev_id = Ext.getCmp('ev_id').value,
        					bo_id = Ext.getCmp('ev_bomid').value,
        					pr_code = Ext.getCmp('ev_prcode').value,
        					grid = Ext.getCmp('bom'),
        					grid1 = Ext.getCmp('grid');
        				if(bo_id==""){bo_id=0;}
        				form.setLoading(true);
            			Ext.Ajax.request({
            				url: basePath + 'scm/sale/bomcost.action',
            				params: {
            					ev_id: ev_id,
            					bo_id: bo_id,
            					pr_code: pr_code
            				},
            				timeout: 600000,
            				callback: function(opt, s, r) {
            					form.setLoading(false);
            					var rs = Ext.decode(r.responseText);
            					if(rs.success) {
            						alert('计算完成!');
            						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'ev_id=' + ev_id});
            						me.GridUtil.loadNewStore(grid, {caller: 'EvaluationRefer', condition: 'evd_evid=' + ev_id});
            						me.GridUtil.loadNewStore(grid1, {caller: caller, condition: 'evd_evid=' + ev_id});
            					} else if(r.exceptionInfo) {
            						showError(r.exceptionInfo);
            					}
            				}
            			});
        			}
        		},
        		'erpBOMInsertButton': {
        			afterrender: function(btn){
    					var status = Ext.getCmp('ev_checkstatuscode');
    					if(status && status.value != 'ENTERING'){
    						btn.hide();
    					}
    				},
        			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        					ev_id = Ext.getCmp('ev_id').value;
        				form.setLoading(true);
            			Ext.Ajax.request({
            				url: basePath + 'scm/sale/bominsert.action',
            				params: {
            					ev_id: ev_id
            				},
            				timeout: 600000,
            				callback: function(opt, s, r) {
            					form.setLoading(false);
            					var rs = Ext.decode(r.responseText);
            					if(rs.success) {
            						alert('导入成功!');
            						me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'evd_evid=' + ev_id});
            					} else if(r.exceptionInfo) {
            						showError(r.exceptionInfo);
            					}
            				}
            			});
        			}
        		},
        		'erpBOMVastCostButton': {
        			click: function(btn) {
        				var form = btn.ownerCt.ownerCt,
        					ev_id = Ext.getCmp('ev_id').value;
        				form.setLoading(true);
            			Ext.Ajax.request({
            				url: basePath + 'scm/sale/bomvastcost.action',
            				params: {
            					ev_id: ev_id
            				},
            				timeout: 600000,
            				callback: function(opt, s, r) {
            					form.setLoading(false);
            					var rs = Ext.decode(r.responseText);
            					if(rs.success) {
            						alert('计算完成!');
            						me.FormUtil.loadNewStore(form, {caller: caller, condition: 'ev_id=' + ev_id});
            						me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'evd_evid=' + ev_id});
            					}
            				}
            			});
        			}
        		},
        		'textfield[name=ev_rate]':{
        			change: function(f){
        				var v = Ext.isEmpty(f.value) ? 1 : f.value,
        					grid = f.up('form').ownerCt.down('grid');
        				var materialcost = Ext.getCmp("ev_materialcost"),
        				   makecost = Ext.getCmp("ev_makecost"),
        				   mancost = Ext.getCmp("ev_mancost"),
        				   othercost = Ext.getCmp("ev_othercost"),
        				   cost = Ext.getCmp("ev_cost");
        				/*if(materialcost && materialcost.value>0){
        					materialcost.setValue(Ext.Number.toFixed(materialcost.value/v,2));
        				}
        				if(makecost && makecost.value >0){
        					makecost.setValue(Ext.Number.toFixed(makecost.value/v,2));
        				}
        				if(mancost && mancost.value >0){
        					mancost.setValue(Ext.Number.toFixed(mancost.value/v,2));
        				}
        				if(othercost && othercost.value>0){
        					othercost.setValue(Ext.Number.toFixed(othercost.value/v,2));
        				}
        				if(cost && cost.value>0){
        					cost.setValue(Ext.Number.toFixed(cost.value/v,2));
        				}*/
        				grid.store.each(function(){
        					this.set('evd_price', Ext.Number.toFixed(this.get('evd_doubleprice')/v, 6));
        					this.set('evd_amount', Ext.Number.toFixed(this.get('evd_amount')/v, 2));
        				});
        			}
        		},
        		'erpTurnOffPriceButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('ev_checkstatuscode');
        				if(status && status.value != 'AUDITED'){
        					btn.hide();
        				}
        			},
        			click: function(b){
        				warnMsg("确定要转入核价单吗?", function(btn){
        					if(btn == 'yes'){
        						me.turnQuotation(b.ownerCt.ownerCt);
        					}
        				});
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
    	turnQuotation: function(form) {
    		var me = this;
    		form.setLoading(true);//loading...
    		Ext.Ajax.request({
    	   		url : basePath + 'scm/evaluation/turnQuotation.action',
    	   		params: {
    	   			id: form.down('#ev_id').value
    	   		},
    	   		method : 'post',
    	   		callback : function(options,success,response){
    	   			form.setLoading(false);
    	   			var localJson = new Ext.decode(response.responseText);
    	   			if(localJson.exceptionInfo){
    	   				showError(localJson.exceptionInfo);
    	   			}
        			if(localJson.success){
        				turnSuccess(function(){
        					var id = localJson.id;
        					var url = "jsps/scm/sale/quotation.jsp?formCondition=qu_idIS"+ id + "&gridCondition=qd_quidIS"+ id;
        					me.FormUtil.onAdd('Quotation' + id, '物料核价单' + id, url);
        				});
    	   			}
    	   		}
    		});
    	},
    	beforeSave:function(){
    		var me = this;
			var form = Ext.getCmp('form');
			if(! me.FormUtil.checkForm()){
				return;
			}
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				me.FormUtil.getSeqId(form);
			}
			var grid1 = Ext.getCmp('grid');
			var grid2 = Ext.getCmp('EvaluationProduct');	
			var grid3 = Ext.getCmp('EvaluationProcess');
			
			var param1 = new Array();
			if(grid1){
				param1 = me.GridUtil.getGridStore(grid1);
			}
			
			var param2 = new Array();
			if(grid2){
				param2 = me.GridUtil.getGridStore(grid2);
			}
			
			var param3 = new Array();
			if(grid3){
				param3 = me.GridUtil.getGridStore(grid3);
			}
			
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				//去除ignore字段
				var keys = Ext.Object.getKeys(r), f;
				var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
				Ext.each(keys, function(k){
					f = form.down('#' + k);
					if(f && f.logic == 'ignore') {
						delete r[k];
					}
					if(k == form.codeField && !Ext.isEmpty(r[k])) {
						r[k] = r[k].trim().replace(reg, '');
					}
				});
				me.save(r, param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}		
    	},
		save: function(){
			var params = new Object();
			var r = arguments[0];
			Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
				if(contains(k, 'ext-', true)){
					delete r[k];
				}
			});	
			params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
			params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
			params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
			params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
			var me = this;
			var form = Ext.getCmp('form');
			Ext.Ajax.request({
		   		url : basePath + form.saveUrl,
		   		params : params,
		   		method : 'post',
		   		callback : function(options,success,response){	   			
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){
	    				saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;
			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   					formCondition+'&gridCondition=evd_evidIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   					formCondition+'&gridCondition=evd_evidIS'+value;
				   		    }
	    				});
		   			} else if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
		   					str = str.replace('AFTERSUCCESS', '');
		   					saveSuccess(function(){
		    					//add成功后刷新页面进入可编辑的页面 
				   				var value = r[form.keyField];
				   		    	var formCondition = form.keyField + "IS" + value ;
	
				   		    	if(me.contains(window.location.href, '?', true)){
					   		    	window.location.href = window.location.href + '&formCondition=' + 
					   		    	formCondition+'&gridCondition=evd_evidIS'+value;
					   		    } else {
					   		    	window.location.href = window.location.href + '?formCondition=' + 
					   		    	formCondition+'&gridCondition=evd_evidIS'+value;
					   		    }
		    				});
		   					showError(str);
		   				} else {
		   					showError(str);
			   				return;
		   				}
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}
		   		
			});
		},
		beforeUpdate:function(){
    		var me = this;
			var form = Ext.getCmp('form');
			if(! me.FormUtil.checkForm()){
				return;
			}
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				me.FormUtil.getSeqId(form);
			}
			var grid1 = Ext.getCmp('grid');
			var grid2 = Ext.getCmp('EvaluationProduct');	
			var grid3 = Ext.getCmp('EvaluationProcess');
			
			var param1 = new Array();
			if(grid1){
				param1 = me.GridUtil.getGridStore(grid1);
			}
			
			var param2 = new Array();
			if(grid2){
				param2 = me.GridUtil.getGridStore(grid2);
			}
			
			var param3 = new Array();
			if(grid3){
				param3 = me.GridUtil.getGridStore(grid3);
			}
			
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				//去除ignore字段
				var keys = Ext.Object.getKeys(r), f;
				var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
				Ext.each(keys, function(k){
					f = form.down('#' + k);
					if(f && f.logic == 'ignore') {
						delete r[k];
					}
					if(k == form.codeField && !Ext.isEmpty(r[k])) {
						r[k] = r[k].trim().replace(reg, '');
					}
				});
				me.update(r, param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}		
    	},
    	update:function(){
			var params = new Object();
			var r = arguments[0];
			Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
				if(contains(k, 'ext-', true)){
					delete r[k];
				}
			});
			params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
			params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
			params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
			params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
			var me = this;
			var form = Ext.getCmp('form');
			Ext.Ajax.request({
		   		url : basePath + form.updateUrl,
		   		params : params,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			//me.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){
	    				saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;
			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   					formCondition;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   					formCondition;
				   		    }
	    				});
		   			} else if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
		   					str = str.replace('AFTERSUCCESS', '');
		   					saveSuccess(function(){
		    					//add成功后刷新页面进入可编辑的页面 
				   				var value = r[form.keyField];
				   		    	var formCondition = form.keyField + "IS" + value ;
				   		    	if(me.contains(window.location.href, '?', true)){
					   		    	window.location.href = window.location.href + '&formCondition=' + 
					   					formCondition;
					   		    } else {
					   		    	window.location.href = window.location.href + '?formCondition=' + 
					   					formCondition;
					   		    }
		    				});
		   					showError(str);
		   				} else {
		   					showError(str);
			   				return;
		   				}
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}
		   		
			});
		},
		contains: function(string,substr,isIgnoreCase){
		    if(isIgnoreCase){
		    	string=string.toLowerCase();
		    	substr=substr.toLowerCase();
		    }
		    var startChar=substr.substring(0,1);
		    var strLen=substr.length;
		    for(var j=0;j<string.length-strLen+1;j++){
		    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
		    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
		    			return true;
		    			}   
		    		}
		    	}
		    return false;
		},
		onSubmit:function(id, allowEmpty, errFn, scope, errFnArgs){
			var me = this;
			var form = Ext.getCmp('form');
			if(form && form.getForm().isValid()){
				var s = me.FormUtil.checkFormDirty(form);
				var grids = Ext.ComponentQuery.query('gridpanel');
				if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
					var param = grids[0].GridUtil.getAllGridStore(grids[0]);//获取必填项都填完整的行
					var param2 = grids[0].GridUtil.getGridStore(grids[0]);//获取修改过且必填项都填完整的行
					Ext.each(grids, function(grid, index){//先校验修改过的行，解决render配置formula导致的dirty，实际不会提示要先保存，导致必填项没有填写仍可以提交的问题
						if(grid.GridUtil){
							var msg = grid.GridUtil.checkGridDirty(grid);
							if(msg.length > 0){
								s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
							}
						}
					});
					if((s == '' || s == '<br/>') && grids[0].necessaryField && grids[0].necessaryField.length > 0  && (allowEmpty !== true)){
						//①明细行没有修改②明细行有修改，但每行必填项都不完整
						var errInfo = grids[0].GridUtil.getInvalid(grids[0]);//获取grid已保存但部分必填字段没填写的行
						if(errInfo.length > 0)
							{showError("明细表有必填字段未完成填写<hr>" + errInfo);return;}
						else if(param == null || param == '')
							{showError("明细表还未添加数据,无法提交!");return;}					
					}
					
				}
				if(s == '' || s == '<br/>'){
					me.FormUtil.submit(id);
				} else {
					Ext.MessageBox.show({
						title:'保存修改?',
						msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
						buttons: Ext.Msg.YESNOCANCEL,
						icon: Ext.Msg.WARNING,
						fn: function(btn){
							if(btn == 'yes'){
								if(typeof errFn === 'function')
									errFn.call(scope, errFnArgs);
								else{
									Ext.getCmp('ev_cost').setValue(Number(ev_materialcost)+Number(ev_makecost)+Number(ev_mancost)+Number(ev_othercost));
									me.beforeUpdate();
								}
							} else if(btn == 'no'){
								var flag = me.FormUtil.checkOriginalForm(form);
								if(!flag)return;
								if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
									if(grids[0].necessaryField && grids[0].necessaryField.length > 0  && (allowEmpty !== true)){
										//①明细行没有修改②明细行有修改，但每行必填项都不完整
										var errInfo = grids[0].GridUtil.getInvalid(grids[0],true);//获取grid已保存但部分必填字段没填写的行								
										if(errInfo.length > 0){
											showError("明细表有必填字段未完成填写<hr>" + errInfo);return;
										}else if( !(param == null || param == '') && grids[0].store.RawData && grids[0].store.RawData.length == 0){
											showError("明细表添加数据后没有保存,无有效数据，无法提交!");return;
										}else if(param == null || param == ''){
											showError("明细表还未添加数据,无法提交!");return;
										}
									}
								}
								me.FormUtil.submit(id);	
							} else {
								return;
							}
						}
					});
				}
			} else {
				me.FormUtil.checkForm();
			}
		}
    });