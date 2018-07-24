		Ext.define('erp.view.ma.printSet.Toolbar', {
		    extend: 'Ext.toolbar.Paging',
		    alias: 'widget.erpPrintSetToolbar',
		    requires: ['erp.view.core.button.VastSave','erp.view.core.button.Refresh','erp.view.core.button.VastGet','erp.view.core.button.VastSendOut','erp.view.core.button.ScanDatalistDetail','erp.view.core.button.ConfirmPeriods'],
		    doRefresh:function(){
		    	window.location.reload();
		    },
		    items: ['-',{
				xtype : 'erpAddDetailButton',
				disabled:false,
				handler: function(btn){
					var grid = btn.ownerCt.ownerCt, store = grid.store;
					var o = {
                            id:0,
                            caller:'',
                            title:'',
                            reportname:'',
                            isdefault: 0,
                            needaudit: 0,
                            nopost: 0,
                            needenoughstock: 0,
                            allowmultiple: -1,
                            countfield: '',
                            statusfield: '',
                            statuscodefield:'',
                            handlermethod:'',
                            defaultcondition:''
                    };
					grid.store.insert(0, o);
				}
			},'-',{
		    	itemId: 'save',
		    	tooltip:'保存',
				iconCls: 'x-button-icon-save',
				width: 24,
				cls: 'x-btn-tb',
		    	handler: function(btn){
					var grid=btn.ownerCt.ownerCt;
					grid.save();
				}
		    },'-',{
		    	itemId: 'close',
		    	tooltip:'删除',
				iconCls: 'x-button-icon-close',
				width: 24,
				cls: 'x-btn-tb',
		    	handler: function(btn){
					var grid=btn.ownerCt.ownerCt;
					grid.deleteRecord();
				}
		    },'-'],
		    updateInfo : function(value){
				var page = this.child('#inputItem').getValue();
				var me = this,
					displayItem = me.child('#displayItem'),
					pageData = me.getPageData();
					pageData.fromRecord = (page-1)*pageSize+1;
					pageData.toRecord = page*pageSize > dataCount ? dataCount : page*pageSize;//
					pageData.total=dataCount;
					dataCount, msg;
					if (displayItem) {
						if (dataCount === 0) {
							msg = me.emptyMsg;
						} else {
							msg = Ext.String.format(
									me.displayMsg,
									pageData.fromRecord,
									pageData.toRecord,
									pageData.total
							);
						}
						displayItem.setText(msg);
						me.doComponentLayout();
					}
					me.child('#last').setDisabled(value==total);
				    me.child('#next').setDisabled(value==total);
				    me.child('#first').setDisabled(value<=1);
				    me.child('#prev').setDisabled(value<=1);
			},
			getPageData : function(){
				var store = this.store,
					totalCount = store.getTotalCount();
				totalCount=dataCount;
				return {
					total : totalCount,
					currentPage : page,
					pageCount: Math.ceil(dataCount / pageSize),
					fromRecord: ((store.currentPage - 1) * store.pageSize) + 1,
					toRecord: Math.min(store.currentPage * store.pageSize, totalCount)
				};
			},
			onPagingKeyDown : function(field, e){
				var me = this, k = e.getKey(), grid = me.ownerCt,
					pageData = me.getPageData(),
					increment = e.shiftKey ? 10 : 1, pageNum = 0, s = Ext.EventObject;
				if (k == s.RETURN) {
					e.stopEvent();
					pageNum = me.readPageFromInput(pageData);
					if (pageNum !== false) {
						pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
						me.child('#inputItem').setValue(pageNum);
						if(me.fireEvent('beforechange', me, pageNum) !== false){
							page = pageNum;
							grid.getData(page, '');
						}
					}
				} else if (k == s.HOME || k == s.END) {
					e.stopEvent();
					pageNum = k == s.HOME ? 1 : pageData.pageCount;
					field.setValue(pageNum);
				} else if (k == s.UP || k == s.PAGEUP || k == s.DOWN || k == s.PAGEDOWN) {
					e.stopEvent();
					pageNum = me.readPageFromInput(pageData);
					if (pageNum) {
						if (k == s.DOWN || k == s.PAGEDOWN) {
							increment *= -1;
						}
						pageNum += increment;
						if (pageNum >= 1 && pageNum <= pageData.pages) {
							field.setValue(pageNum);
						}
					}
				}
				me.updateInfo(pageNum);
			}, 
			moveFirst : function(){
				var me = this;
				me.child('#inputItem').setValue(1);
				value = 1;
				page = value;
				me.ownerCt.getData(1, '');
				me.updateInfo(value);
			},
			movePrevious : function(){
				var me = this;
				me.child('#inputItem').setValue(me.child('#inputItem').getValue()-1);
				value = me.child('#inputItem').getValue();
				page = value;
				me.ownerCt.getData(page, '');
				me.updateInfo(value);
			},
			moveNext : function(){
				var me = this,
					last = me.getPageData().pageCount;
				total = last;
				me.child('#inputItem').setValue(me.child('#inputItem').getValue()+1);
				value = me.child('#inputItem').getValue();
				page = value;
				me.ownerCt.getData(page, '');
				me.updateInfo(value);
			},
			moveLast : function(){
				var me = this,
				last = me.getPageData().pageCount;
				total = last;
				me.child('#inputItem').setValue(last);
				value = me.child('#inputItem').getValue();
				page = value;
				me.ownerCt.getData(page, '');
				me.updateInfo(value);
			},
			onLoad : function() {
				var e = this, d, b, c, a;
				if (!e.rendered) {
					return;
				}
				d = e.getPageData();
				b = d.currentPage;
				c = Math.ceil(dataCount / pageSize);
				a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
				e.child("#afterTextItem").setText(a);
				e.child("#inputItem").setValue(b);
				e.child("#first").setDisabled(b === 1);
				e.child("#prev").setDisabled(b === 1);
				e.child("#next").setDisabled(b === c || c===1);//
				e.child("#last").setDisabled(b === c || c===1);
				e.child("#refresh").enable();
				e.updateInfo();
				e.fireEvent("change", e, d);
			},
			onPagingBlur : function(e){
		        var inputItem = this.child("#inputItem"),
		            curPage;
		        if (inputItem) {
		            curPage = this.getPageData().currentPage;
		            var e = this, d, b, c, a;
		    		d = e.getPageData();
		    		b = d.currentPage;
		    		c = Math.ceil(dataCount / pageSize);
		    		a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
		    		e.child("#afterTextItem").setText(a);
		    		e.child("#inputItem").setValue(b);
		    		e.child("#first").setDisabled(b === 1);
		    		e.child("#prev").setDisabled(b === 1);
		    		e.child("#next").setDisabled(b === c || c===1);
		    		e.child("#last").setDisabled(b === c || c===1);
		        }
		    },
			afterOnLoad : function(num) {
				var e = this, d, c, a, grid = e.ownerCt;
				if (!e.rendered) {
					return;
				}
				d = e.getPageData();
				b = d.currentPage;
				c = Math.ceil(dataCount / pageSize);
				a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
				e.child("#afterTextItem").setText(a);
				if(num && num == 1) e.child("#inputItem").setValue(1);
				e.updateInfo();
				e.fireEvent("change", e, d);
				e.child('#last').setDisabled(c <= 1 || page == c);
				e.child('#next').setDisabled(c <= 1 || page == c);
			}
		});