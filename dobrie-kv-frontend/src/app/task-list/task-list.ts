import { ChangeDetectorRef, Component, model, OnInit, signal, ViewChild, ViewContainerRef } from '@angular/core';
import { Task } from '../models/task.model';
import { TaskService } from '../services/task.service';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { ModalForm } from '../modal-form/modal-form';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-list.html',
  styleUrl: './task-list.css',
})
export class TaskList implements OnInit {
  
  tasks$!: Observable<Task[]>;

  @ViewChild('modalContainer', { read: ViewContainerRef })
  container!: ViewContainerRef;

  public searchValue = signal<number>(0);

  public currentPage = 0;
  public pageSize = 10;
  public totalPages = 0;
  public totalElements = 0; 

  constructor(private taskService: TaskService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.tasks$ = this.taskService.getTasks().pipe(
      map(res => res.content)  
    );
  }

  refreshTasks() {
    this.tasks$ = this.taskService.getTasks(this.currentPage, this.pageSize).pipe(
      tap(res => {
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
      }),
      map(res => res.content)
    );
    this.cdr.markForCheck();
  }

  public openModal(taskToEdit?: Task) {
    this.container.clear();
    const modalRef = this.container.createComponent(ModalForm);

    if (taskToEdit) {
      modalRef.instance.taskModel.set({ ...taskToEdit });
    }

    const closeSub = modalRef.instance.close.subscribe(() => {
      this.container.clear();
      closeSub.unsubscribe();
      saveSub.unsubscribe();
    });

    const saveSub = modalRef.instance.save.subscribe((data) => {
      console.log("DATA", data);
      if(data.id && data.id !== 0){
        this.taskService.updateTask(data.id, data.status).subscribe({
          next: (updatedTask) => {
            console.log('Статус обновлен:', updatedTask);
            this.refreshTasks();
            this.container.clear();
            this.cdr.markForCheck();
          },
          error: (err) => {
            console.error('Ошибка при обновлении статуса:', err);
          }
        });
      } else {
        this.taskService.createTask(data).subscribe({
          next: () => {
            this.refreshTasks();
            this.container.clear();
            closeSub.unsubscribe();
            saveSub.unsubscribe();
            this.cdr.markForCheck(); 
          },
          error: (err) => {
            console.error('Ошибка запроса:', err);
          }
        });
      } 
    });
  }

  getTaskById(id: number) {
    this.tasks$ = this.taskService.getTaskById(id).pipe(
      tap(r => console.log('Полученная задача:', r)),
      map(task => [task])
    )    
  }

  deleteTask(id: number) {
    this.taskService.deleteTask(id)
    .pipe(
      tap(r => console.log('Полученная задача:', r)),
    )
    .subscribe({
      next: () => {
        this.refreshTasks();
      },
      error: (err) => {
      console.error('Ошибка при удалении из базы:', err);
      }
    });

    
  }
}
