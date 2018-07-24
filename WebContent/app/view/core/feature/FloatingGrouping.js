/**
 * grid滚动时，智能浮动的分组
 */
Ext.define('erp.view.core.feature.FloatingGrouping', {
	extend : 'Ext.grid.feature.Grouping',
	alias : 'feature.floatinggrouping',
	init : function(grid) {
		var me = this, view = me.view;
		me.callParent(arguments);
		view.floatedEl = {};
		view.on({
			afterrender : function() {
				view.getEl().on('scroll', me.onScroll, me);
			},
			resize : me.resizeFloatedEl,
			scope : me
		});
	},
	onScroll : function() {
		var me = this, groupCache = me.groupCache, groupName;
		for (groupName in groupCache) {
			if (me.isExpanded(groupName)) {
				var visible = me.isHeaderVisible(groupName), floating = me.isFloating(groupName);
				if (visible && floating) {
					me.unfloating(groupName);
					if (me.lockingPartner && me.lockingPartner.groupCache) {
						me.lockingPartner.unfloating(groupName);
					}
				} else if (!visible && !floating) {
					me.floating(groupName);
					if (me.lockingPartner && me.lockingPartner.groupCache) {
						me.lockingPartner.floating(groupName);
					}
				}
			}
		}
	},
	isHeaderVisible : function(groupName) {
		var me = this, header = me.getHeaderNode(groupName);
		return header.isVisible() && header.getY() >= me.view.getEl().getY();
	},
	isFloating : function(groupName) {
		return this.getGroup(groupName).floating;
	},
	floating : function(groupName) {
		var me = this, group = me.getGroup(groupName), el = me.view.getEl(), floatingEl = me.view.floatedEl[groupName];
		if (!floatingEl) {
			var header = me.getHeaderNode(groupName);
			floatingEl = me.view.floatedEl[groupName] = el.createChild({
				// getGroupName方法要求ID格式为(.+)-hd-(.+)
				'id' : 'floating-' + header.id
			}).applyStyles({
				'position' : 'fixed',
				'border-top' : '1px solid #fff',
				'left' : el.getX() + 'px',
				'top' : el.getY() + 'px',
				'width' : header.getWidth() + 'px'
			});
			floatingEl.syncContent(header);
		} else {
			floatingEl.applyStyles({
				'display' : 'block'
			});
		}
		group.floating = true;
	},
	unfloating : function(groupName) {
		var me = this, group = me.getGroup(groupName);
		me.view.floatedEl[groupName].applyStyles({
			'display' : 'none'
		});
		group.floating = null;
	},
	// override
	onGroupClick : function(view, rowElement, groupName, e) {
		var me = this;
		if (me.isFloating(groupName)) {
			me.unfloating(groupName);
			if (me.lockingPartner && me.lockingPartner.groupCache) {
				me.lockingPartner.unfloating(groupName);
			}
		}
		var header = me.getHeaderNode(groupName);
		var count = 1;
		var grid = view.panel;
		if(grid.isLocked){
			grid = grid.ownerCt;
			count = 4;
		}
		grid.scrollByDeltaY(header.getY()-header.getHeight()*count);
		me.callParent(arguments);
	},
	// resize when view resized
	resizeFloatedEl : function() {
		var me = this, groupCache = me.groupCache, groupName, header, floatingEl, el = me.view.getEl(), style = {
			'left' : el.getX() + 'px',
			'top' : el.getY() + 'px'
		};
		for (groupName in groupCache) {
			header = me.getHeaderNode(groupName);
			floatingEl = me.view.floatedEl[groupName];
			if (floatingEl) {
				floatingEl.applyStyles(Ext.apply(style, {
					width : header.getWidth() + 'px'
				}));
			}
		}
	}
});