package book.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import book.dto.BookTransactionDTO;
import book.entity.BookTransactionEntity;
import book.repository.BookTransactionRepository;
import book.service.IBookTransactionService;

@Service
public class BookTransactionService implements IBookTransactionService {
	@Autowired
	private BookTransactionRepository transactionRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public BookTransactionDTO save(BookTransactionDTO dto) {
		BookTransactionEntity entity = modelMapper.map(dto, BookTransactionEntity.class);
		entity = transactionRepository.save(entity);
		return modelMapper.map(entity, BookTransactionDTO.class);
	}

	@Override
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			transactionRepository.deleteById(id);
		}
	}

	@Override
	public List<BookTransactionDTO> findAll(Pageable pageable) {
		return transactionRepository.findAll(pageable).getContent().stream()
				.map(entity -> modelMapper.map(entity, BookTransactionDTO.class)).collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) transactionRepository.count();
	}

}
