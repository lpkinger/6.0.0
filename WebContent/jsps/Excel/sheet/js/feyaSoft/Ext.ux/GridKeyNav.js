/**
 * @class Ext.ux.GridKeyNav
 * Simple plugin to implement basic keyboard navigation for Ext 2.x grids that have a
 * paging toolbar (required).  Adds: Page Down/Page Up/Right Arrow/Left Arrow/Home/End,
 * also adds functionality to the selection model to see if Down is pressed at the last record
 * of a page or Up is pressed at the first record of the page and attempts to move a page
 * in the proper direction.
 */
Ext.ux.GridKeyNav = function() {}
Ext.ux.GridKeyNav.prototype = {
    /**
     * @cfg {Boolean} bottomBar
     * Look for the paging toolbar within the grids bbar, set to true to pull from tbar (defaults to true)
     */
    bottomBar: true,
    /**
     * @cfg {Ext.PagingToolbar} toolbar
     * Manually pass in a toolbar instead of pulling from bbar/tbar of the grid (in case it is rendered elsewhere)
     */
    toolbar: null,

    init: function(grid) {
        this.grid = grid;
        this.grid.on({
            render: this.onRender,
            destroy: this.onDestroy,
            scope: this
        });
    },

    onRender : function() {
        this.nav = new Ext.KeyNav(this.grid.getGridEl(),{
            left: this.pagingNav.createDelegate(this,['prev']),
            right: this.pagingNav.createDelegate(this,['next']),
            pageDown: this.pagingNav.createDelegate(this,['next']),
            pageUp: this.pagingNav.createDelegate(this, ['prev']),
            home: this.pagingNav.createDelegate(this,['first']),
            end: this.pagingNav.createDelegate(this,['last'])
        });

        this.pt = this.toolbar||((this.bottomBar)?this.grid.getBottomToolbar():this.grid.getTopToolbar());
        this.sm = this.grid.getSelectionModel();
        this.sm.selectNext = this.sm.selectNext.createInterceptor(this.beforeNext, this);
        this.sm.selectPrevious = this.sm.selectPrevious.createInterceptor(this.beforePrevious, this);
    },

    onDestroy: function() {
        this.nav.disable();
        delete this.nav;
    },

    pagingNav: function(page) {
        if (!this.pt[page].disabled) {
            this.pt.onClick(this.pt[page]);
            if (page === 'last') {
                this.forceSelectLastRow();
            }
        } else {
            // Is all the data on a single page? If so let home/end work
            if (this.pt.pageSize >= this.grid.getStore().data.length) {
                if (page == 'first') {
                    this.sm.selectFirstRow();
                } else if (page == 'last') {
                    this.sm.selectLastRow();
                }
            }
        }
    },

    beforeNext: function() {
        if (!this.sm.hasNext()) {
            this.pagingNav('next');
            return false;
        }
    },

    beforePrevious: function() {
        if (!this.sm.hasPrevious()) {
            this.pagingNav('prev');
            this.forceSelectLastRow();
            return false;
        }
    },

    forceSelectLastRow: function() {
        this.grid.getStore().on('load', function() {
            this.sm.selectLastRow();
        }, this, {single: true});
    }
}