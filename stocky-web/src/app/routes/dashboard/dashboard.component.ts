import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {PassportUsecase} from "../passport/authentication/_usecase/passport.usecase";
import {LowStockAlertUsecase} from '../stock/_usecase/low-stock-alert.usecase';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styles: [`
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 16px;
            margin-top: 16px;
        }
        .stat-card {
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .stat-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transform: translateY(-2px);
        }
        .stat-icon {
            font-size: 48px;
            line-height: 1;
        }
        .stat-number {
            font-size: 32px;
            font-weight: 600;
            line-height: 1.2;
        }
        .stat-label {
            font-size: 14px;
            color: rgba(0, 0, 0, 0.65);
            margin-top: 8px;
        }
        .warning-card {
            background: linear-gradient(135deg, #fff7e6 0%, #ffe58f 100%);
            border: 1px solid #faad14;
        }
        .warning-card .stat-number {
            color: #fa8c16;
        }
        .warning-card .stat-icon {
            color: #faad14;
        }
        .danger-card {
            background: linear-gradient(135deg, #fff2f0 0%, #ffa39e 100%);
            border: 1px solid #ff4d4f;
        }
        .danger-card .stat-number {
            color: #ff4d4f;
        }
        .danger-card .stat-icon {
            color: #ff4d4f;
        }
    `],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();
    public lowStockCount = 0;
    public isLoadingLowStock = false;

    constructor(
        private passportUsecase: PassportUsecase,
        private lowStockUsecase: LowStockAlertUsecase,
        private router: Router
    ) {}

    public ngOnInit(): void {
        this.loadLowStockCount();
    }

    public ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    public getFullName() {
        return this.passportUsecase.getLoggedInUser()?.username;
    }

    private loadLowStockCount(): void {
        this.isLoadingLowStock = true;
        this.lowStockUsecase.getLowStockCount()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (count) => {
                    this.lowStockCount = count;
                    this.isLoadingLowStock = false;
                },
                error: () => {
                    this.lowStockCount = 0;
                    this.isLoadingLowStock = false;
                }
            });
    }

    public navigateToLowStock(): void {
        this.router.navigate(['/stock/view-low-stock']);
    }

    public getLowStockCardClass(): string {
        if (this.lowStockCount > 10) {
            return 'danger-card';
        }
        if (this.lowStockCount > 0) {
            return 'warning-card';
        }
        return '';
    }
}
